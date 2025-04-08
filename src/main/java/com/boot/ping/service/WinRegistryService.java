package com.boot.ping.service;

import com.boot.ping.dto.RegistryCommandDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WinRegistryService {

    public void executeCommand(RegistryCommandDto commandDto) throws IOException, InterruptedException {
        System.out.println("실행 명령어: " + commandDto.toCommand());
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", commandDto.toCommand());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("출력: " + line);
        }
        int exitCode = process.waitFor();
        System.out.println("프로세스 종료 코드: " + exitCode);
        if (exitCode != 0) {
            throw new IOException("명령어 실행 실패, 종료 코드: " + exitCode);
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



}
