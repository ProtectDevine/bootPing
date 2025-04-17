package com.boot.ping.service;

import com.boot.ping.dto.TaskDto;
import com.boot.ping.enums.AlertCodes;
import com.boot.ping.enums.TaskWhiteList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {

    public List<TaskDto> getTasks() throws IOException {
        List<TaskDto> tasks = new ArrayList<>();
        Process process = Runtime.getRuntime().exec("tasklist");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS949"));
        String line;

        for (int i = 0; i < 3; i++) {
            reader.readLine();
        }

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            // 디버깅용: 전체 줄 출력
            System.out.println("원본 줄: " + line);

            String[] parts = line.split("\\s+", 5);
            if (parts.length < 5) {
                System.err.println("파싱 실패 (열 부족): " + line);
                continue;
            }

            // 디버깅용: parts 배열 출력
            System.out.println("파싱 결과: " + Arrays.toString(parts));

            try {
                String name = parts[0];
                int pid = parts[1].matches("\\d+") ? Integer.parseInt(parts[1]) : -1;
                String sessionName = parts[2];
                int sessionNumber = parts[3].matches("\\d+") ? Integer.parseInt(parts[3]) : -1;
                String memoryUsageStr = parts[4];

                Long memoryUsage = parseMemoryUsage(memoryUsageStr);

                TaskDto processInfo = new TaskDto(name, pid, sessionName, sessionNumber, memoryUsage, new SimpleBooleanProperty(false));
                tasks.add(processInfo);
            } catch (Exception e) {
                System.err.println("파싱 중 오류: " + line + " | 오류: " + e.getMessage());
                AlertCodes.alertDisplay(AlertCodes.TASKS_GET_FAIL);
            }
        }
        reader.close();

        tasks = TaskWhiteList.filterTasks(tasks);

        return tasks.stream()
                .sorted(Comparator.comparing(TaskDto::getMemoryUsage, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private static Long parseMemoryUsage(String memoryUsageStr) {
        try {
            String cleanStr = memoryUsageStr.replace(",", "").trim();
            String[] parts = cleanStr.split("\\s+");

            // 숫자가 포함된 마지막 부분을 찾음
            String numberPart = null;
            for (int i = parts.length - 1; i >= 0; i--) {
                if (parts[i].matches("\\d+")) { // 숫자인지 확인
                    numberPart = parts[i];
                    break;
                }
            }

            if (numberPart == null) {
                throw new IllegalArgumentException("숫자 부분이 없음");
            }

            long value = Long.parseLong(numberPart);

            // 숫자 단위(K, M, G)가 있으면 적용, 마지막이 숫자가 아니면 단위로 간주
            if (parts.length > 1) {
                String lastPart = parts[parts.length - 1].toUpperCase();
                if (!lastPart.matches("\\d+")) {
                    return switch (lastPart) {
                        case "K" -> value * 1024L;
                        case "M" -> value * 1024L * 1024L;
                        case "G" -> value * 1024L * 1024L * 1024L;
                        default -> value;
                    };
                }
            }
            return value; // 단위 없으면 바이트 단위로 간주
        } catch (Exception e) {
            System.err.println("메모리 사용량 파싱 실패 - 입력: " + memoryUsageStr + " | 오류: " + e.getMessage());
            return 0L;
        }
    }

    // TODO TaskController 포함 하드코딩 개선 후 다국어 적용
    public void terminateAllTasks(List<TaskDto> tasks) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("모든 태스크 종료");
        confirmation.setHeaderText("모든 비시스템 태스크를 종료하시겠습니까?");
        confirmation.setContentText("필수 시스템 태스크는 제외됩니다.");

        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }

        int terminatedCount = 0;
        for (TaskDto task : tasks) {

            try {
                String command = "taskkill /F /PID " + task.getPid();
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                System.out.println("종료 완료: " + task.getName() + " (PID: " + task.getPid() + ")");
                terminatedCount++;
            } catch (IOException | InterruptedException e) {
                System.err.println("태스크 종료 실패: " + task.getName() + " | 오류: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("종료 실패");
                alert.setContentText("태스크 종료 실패: " + task.getName() + "\n오류: " + e.getMessage());
                alert.showAndWait();
            }
        }

        Alert result = new Alert(Alert.AlertType.INFORMATION);
        result.setTitle("종료 완료");
        result.setContentText(terminatedCount + "개의 태스크가 종료되었습니다.");
        result.showAndWait();

        refreshTable(tasks);
    }

    public void terminateTasks(List<TaskDto> tasks) {
        int terminatedCount = 0;
        for (TaskDto task : tasks) {

            try {
                String command = "taskkill /F /PID " + task.getPid();
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                System.out.println("종료 완료: " + task.getName() + " (PID: " + task.getPid() + ")");
                terminatedCount++;
            } catch (IOException | InterruptedException e) {
                System.err.println("태스크 종료 실패: " + task.getName() + " | 오류: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("종료 실패");
                alert.setContentText("태스크 종료 실패: " + task.getName() + "\n오류: " + e.getMessage());
                alert.showAndWait();
            }
        }

        Alert result = new Alert(Alert.AlertType.INFORMATION);
        result.setTitle("종료 완료");
        result.setContentText(terminatedCount + "개의 태스크가 종료되었습니다.");
        result.showAndWait();

        refreshTable(tasks);
    }

    public ObservableList<TaskDto> refreshTable(List<TaskDto> tasks) {
        return FXCollections.observableArrayList(tasks);
    }


}
