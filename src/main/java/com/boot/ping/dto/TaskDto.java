package com.boot.ping.dto;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TaskDto {
    private String name;
    private int pid;
    private String sessionName;
    private int sessionNumber;
    private Long memoryUsage;
    private final BooleanProperty selected;

    public TaskDto(String name, int pid, String sessionName, int sessionNumber, Long memoryUsage) {
        this.name = name;
        this.pid = pid;
        this.sessionName = sessionName;
        this.sessionNumber = sessionNumber;
        this.memoryUsage = memoryUsage;
        this.selected = new SimpleBooleanProperty(false);
    }

    public BooleanProperty getSelected() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get(); // boolean 값 반환
    }

    @Override
    public String toString() {
        return String.format(
                "TaskDto{name='%s', pid=%d, sessionName='%s', sessionNumber=%d, memoryUsage=%d, selected=%b}",
                name, pid, sessionName, sessionNumber, memoryUsage, selected.get());
    }
}