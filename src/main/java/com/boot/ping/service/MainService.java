package com.boot.ping.service;

import com.boot.ping.MainResponseDto;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class MainService {

    public MainResponseDto.PingDto getPingMessage() throws IOException {

        InetAddress target = InetAddress.getByName("www.google.com");
        boolean isConnect = target.isReachable(1000);
        List<Long> startPings = new ArrayList<>();
        List<Long> endPings = new ArrayList<>();
        String responseMessage = "";

        if (isConnect) {

            int i;
            for (i = 0; i <= 5; i++) {
                Long startPing = System.nanoTime();
                boolean reConnect = target.isReachable(1000);
                Long endPing = System.nanoTime();
                startPings.add(startPing);
                endPings.add(endPing);
            }

            List<Long> averagePings =
                    LongStream.range(0, endPings.size())
                            .mapToObj(k -> endPings.get((int) k) - startPings.get((int) k))
                            .toList();

            Long average = (long) averagePings.stream().mapToLong(Long::longValue).average().orElse(0);

            responseMessage = average / 1_000_000 + "평균 ms";

        } else {

            responseMessage = "connection lost";

        }

        return  MainResponseDto.PingDto.builder()
                .responsePing(responseMessage)
                .build();
    }


    public void runOptimization() {
        try {
            String networkInterfaceGUID = getNetworkInterfaceGUID();

            if (null == networkInterfaceGUID || networkInterfaceGUID.isEmpty()) {
                showAlert("오류", "네트워크 인터페이스 GUID를 찾을 수 없습니다.");
            }
            String networkPath = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces\\" + networkInterfaceGUID;

            // TCPAckFrequency 확인
            int tcpAckFrequency = checkRegistryValue(networkPath, "TcpAckFrequency");
            if (tcpAckFrequency != 1) {
                executeCommand("reg add \"" + networkPath + "\" /v TcpAckFrequency /t REG_QWORD /d 1 /f");
            }

            // TCPNoDelay 확인
            int tcpNoDelay = checkRegistryValue(networkPath, "TCPNoDelay");
            if (tcpNoDelay != 1) {
                executeCommand("reg add \"" + networkPath + "\" /v TCPNoDelay /t REG_QWORD /d 1 /f");
            }

            // MSMQ TCPNoDelay 확인
            int msmqTcpNoDelay = checkRegistryValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSMQ\\Parameters", "TCPNoDelay");
            if (msmqTcpNoDelay != 1) {
                executeCommand("reg add \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSMQ\\Parameters\" /v TCPNoDelay /t REG_QWORD /d 1 /f");
            }

            // NetworkThrottlingIndex 확인
            int networkThrottlingIndex = checkRegistryValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Multimedia\\SystemProfile", "NetworkThrottlingIndex");
            if (networkThrottlingIndex != 0xFFFFFFFF) {
                executeCommand("reg add \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Multimedia\\SystemProfile\" /v NetworkThrottlingIndex /t REG_DWORD /d 0xFFFFFFFF /f");
            }

            showAlert("완료", "네트워크 최적화 설정이 완료되었습니다.");
        } catch (Exception ex) {
            showAlert("오류 발생", "네트워크 최적화 실행 중 오류가 발생했습니다.\n" + ex.getMessage());
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

    public int checkRegistryValue(String path, String key) {
        try {

            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "reg query " + path + " /v " + key);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(key)) {
                    String[] parts = line.trim().split("\\s+"); // 공백을 기준으로 분할
                    if (parts.length >= 3) {
                        String hexValue = parts[parts.length - 1].replace("0x", ""); // "0x" 제거
                        return Integer.parseInt(hexValue, 16); // 16진수 값 변환
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
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
