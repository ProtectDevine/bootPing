package com.boot.ping.service;

import com.boot.ping.MainResponseDto;
import com.boot.ping.dto.RegistryCommandDto;
import com.boot.ping.enums.AlertCodes;
import com.boot.ping.strategy.OptimizationStrategy;
import com.boot.ping.strategy.RegistryStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainService {

    private final WinRegistryService winRegistryService = new WinRegistryService();
    private List<OptimizationStrategy> strategies = new ArrayList<>();

    public MainService() {
        this.strategies = List.of(
                new RegistryStrategy.TcpAckFrequencyStrategy(),
                new RegistryStrategy.TcpNoDelayStrategy(),
                new RegistryStrategy.MsmqTcpNoDelayStrategy(),
                new RegistryStrategy.NetworkThrottlingStrategy()
        );
    }

    public MainResponseDto.PingDto getPingMessage() throws IOException {

        InetAddress target = InetAddress.getByName("www.google.com");
        boolean isConnect = target.isReachable(1000); // 초기 연결 확인
        List<Long> pingTimes = new ArrayList<>();
        String responseMessage;

        if (isConnect) {
            for (int i = 0; i < 3; i++) {
                long startPing = System.nanoTime();
                target.isReachable(1000);
                long endPing = System.nanoTime();
                pingTimes.add(endPing - startPing); // 나노초 단위로 저장
            }

            // 평균 계산 (나노초 -> 밀리초로 변환)
            long averageNs = (long) pingTimes.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0L);
            long averageMs = averageNs / 1_000_000;

            // 소수점 2자리까지 포맷팅
            responseMessage = String.format("%d 평균 ms", averageMs);
        } else {
            responseMessage = AlertCodes.alertDisplay(AlertCodes.PING_CHECK_FAIL);
        }

        return MainResponseDto.PingDto.builder()
                .responsePing(responseMessage)
                .build();
    }

    public boolean runOptimization(String guid, boolean hasTcpNoDelay) {

        try {
            String networkPath = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces\\" + guid;

            if (hasTcpNoDelay) {
                applyInitialSettings(networkPath);
                AlertCodes.alertDisplay(AlertCodes.RESET_BOOST_COMPLETE);

            } else {
                for (OptimizationStrategy strategy : strategies) {
                    strategy.apply(winRegistryService, networkPath);
                }
                AlertCodes.alertDisplay(AlertCodes.BOOST_PING_COMPLETE);
            }

        } catch (Exception ex) {
            AlertCodes.alertDisplay(AlertCodes.RESET_BOOST_FAIL, ex.getMessage());
        }

        return !hasTcpNoDelay;

    }

    private void applyInitialSettings(String networkPath) throws IOException, InterruptedException {

        try {

            winRegistryService.executeCommand(RegistryCommandDto.builder()
                    .path(networkPath)
                    .valueName("TcpAckFrequency")
                    .data(0L)
                    .build());
            System.out.println("TcpAckFrequency 설정 완료");
            winRegistryService.executeCommand(RegistryCommandDto.builder()
                    .path(networkPath)
                    .valueName("TCPNoDelay")
                    .data(0L)
                    .build());
            System.out.println("TCPNoDelay 설정 완료");
            winRegistryService.executeCommand(RegistryCommandDto.builder()
                    .path("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSMQ\\Parameters")
                    .valueName("TCPNoDelay")
                    .data(0L)
                    .build());
            System.out.println("MSMQ TCPNoDelay 설정 완료");
            winRegistryService.executeCommand(RegistryCommandDto.builder()
                    .path("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Multimedia\\SystemProfile")
                    .valueName("NetworkThrottlingIndex")
                    .type("REG_DWORD")
                    .data(0x00000000L)
                    .build());
            System.out.println("NetworkThrottlingIndex 설정 완료");

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public long checkRegistryValue(String path, String key) {

        try {

            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "reg query \"" + path + "\" /v " + key);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("레지스트리 출력: " + line);
                if (line.contains(key) && (line.contains("REG_DWORD") || line.contains("REG_QWORD"))) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 3) {
                        String hexValue = parts[parts.length - 1];
                        System.out.println("파싱할 값: " + hexValue);
                        if (hexValue.startsWith("0x")) {
                            return Long.parseLong(hexValue.replace("0x", ""), 16);
                        } else {
                            return Long.parseLong(hexValue); // 10진수 처리
                        }
                    }
                }
            }
            int exitCode = process.waitFor();
            System.out.println("명령어 종료 코드: " + exitCode);
            System.out.println("키를 찾지 못함: " + key);
            return -1L; // 키 없음 또는 파싱 실패
        } catch (Exception e) {
            System.err.println("레지스트리 조회 오류: " + e.getMessage());
            e.printStackTrace();
            return -1L;
        }

    }

    public String getNetworkInterfaceGUID() {

        try {

            // 현재 IP 주소 가져오기
            InetAddress localHost = InetAddress.getLocalHost();
            String currentIP = localHost.getHostAddress();

            // 레지스트리에서 인터페이스 목록 확인
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "reg query HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces /s");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String currentGUID = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("HKEY_LOCAL_MACHINE")) {
                    currentGUID = line.substring(line.lastIndexOf("\\") + 1);
                }
                if (line.contains("DhcpIPAddress") || line.contains("IPAddress")) {
                    if (line.contains(currentIP)) {
                        return currentGUID; // 현재 IP와 일치하는 GUID 반환
                    }
                }
            }
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getGUID() {

        String networkInterfaceGUID = getNetworkInterfaceGUID();
        if ("".equals(networkInterfaceGUID) || networkInterfaceGUID.isEmpty()) {
            AlertCodes.alertDisplay(AlertCodes.GUID_CHECK_FAIL);
        }
        return networkInterfaceGUID;

    }

    public boolean checkTcpNoDelay(String guid) {

        String networkPath = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces\\" + guid;

        long tcpNoDelay = checkRegistryValue(networkPath, "TCPNoDelay");
        System.out.println("tcpNoDelay: " + tcpNoDelay);

        return tcpNoDelay != 0L;
    }

}
