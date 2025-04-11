package com.boot.ping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskDto {

    private String name;
    private int  pid;
    private String sessionName;
    private int sessionNumber;
    private Long memoryUsage;



    @Override
    public String toString() {
        return String.format(
                "ProcessInfo{imageName='%s', pid=%d, sessionName='%s', sessionNumber=%d, memoryUsage='%s'}",
                name, pid, sessionName, sessionNumber, memoryUsage);
    }

}
