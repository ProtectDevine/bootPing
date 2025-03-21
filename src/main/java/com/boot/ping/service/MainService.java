package com.boot.ping.service;

import com.boot.ping.MainResponseDto;
import com.boot.ping.enums.AlertCodes;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainService {

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


    public void runOptimization() {
        try {
            String networkInterfaceGUID = getNetworkInterfaceGUID();
            if ("".equals(networkInterfaceGUID) || networkInterfaceGUID.isEmpty()) {
                AlertCodes.alertDisplay(AlertCodes.GUID_CHECK_FAIL);
                return;
            }
            String networkPath = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces\\" + networkInterfaceGUID;

            // TCPAckFrequency 확인
            long tcpAckFrequency = checkRegistryValue(networkPath, "TcpAckFrequency");
            System.out.println("TcpAckFrequency: " + tcpAckFrequency);
            if (tcpAckFrequency != 1L) {
                executeCommand("reg add \"" + networkPath + "\" /v TcpAckFrequency /t REG_QWORD /d 1 /f");
            }

            // TCPNoDelay 확인
            long tcpNoDelay = checkRegistryValue(networkPath, "TCPNoDelay");
            System.out.println("TCPNoDelay: " + tcpNoDelay);
            if (tcpNoDelay != 1L) {
                executeCommand("reg add \"" + networkPath + "\" /v TCPNoDelay /t REG_QWORD /d 1 /f");
            }

            // MSMQ TCPNoDelay 확인
            long msmqTcpNoDelay = checkRegistryValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSMQ\\Parameters", "TCPNoDelay");
            System.out.println("MSMQ TCPNoDelay: " + msmqTcpNoDelay);
            if (msmqTcpNoDelay != 1L) {
                executeCommand("reg add \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSMQ\\Parameters\" /v TCPNoDelay /t REG_QWORD /d 1 /f");
            }

            // NetworkThrottlingIndex 확인
            long networkThrottlingIndex = checkRegistryValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Multimedia\\SystemProfile", "NetworkThrottlingIndex");
            System.out.println("NetworkThrottlingIndex: " + networkThrottlingIndex);
            if (networkThrottlingIndex != 0xFFFFFFFFL) { // long 리터럴로 비교
                executeCommand("reg add \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Multimedia\\SystemProfile\" /v NetworkThrottlingIndex /t REG_DWORD /d 0xFFFFFFFF /f");
            }

            AlertCodes.alertDisplay(AlertCodes.BOOST_PING_COMPLETE);

        } catch (Exception ex) {

            AlertCodes.alertDisplay(AlertCodes.BOOST_PING_FAIL, ex.getMessage());

        }
    }

    private void executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        process.waitFor();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
                            return Long.parseLong(hexValue.replace("0x", ""), 16); // Long으로 파싱
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
            System.out.println("현재 IP 주소: " + currentIP);

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
                if (line.contains("DhcpIPAddress") || line.contains("IPAddress")) { // 둘 다 확인
                    if (line.contains(currentIP)) {
                        return currentGUID; // 현재 IP와 일치하는 GUID 반환
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 찾지 못하면 null 반환
    }

}
