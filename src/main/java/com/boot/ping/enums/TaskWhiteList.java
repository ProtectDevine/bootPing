package com.boot.ping.enums;

import com.boot.ping.dto.TaskDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum TaskWhiteList {


    WHITE_LIST(
            Arrays.asList(
                    "bootping.exe",
                    "System Idle Process",
                    "System",
                    "smss.exe",
                    "csrss.exe",
                    "wininit.exe",
                    "services.exe",
                    "lsass.exe",
                    "svchost.exe",
                    "winlogon.exe",
                    "explorer.exe",
                    "conhost.exe",
                    "taskmgr.exe",
                    "cmd.exe"
            )
    );

    private List<String> whiteTasks;

    public static boolean isSystemTask(String taskName) {
        return WHITE_LIST.getWhiteTasks()
                .stream()
                .anyMatch(x -> x.equalsIgnoreCase(taskName));
    }

    public static List<TaskDto> filterTasks(List<TaskDto> tasks) {
        return tasks.stream().filter(task -> !isSystemTask(task.getName()))
                .collect(Collectors.toList());
    }



}
