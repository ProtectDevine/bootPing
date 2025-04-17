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
    private int  pid;
    private String sessionName;
    private int sessionNumber;
    private Long memoryUsage;
    @Builder.Default private final BooleanProperty selected = new SimpleBooleanProperty(false);


    @Override
    public String toString() {
        return String.format(
                "TaskDto{name='%s', pid=%d, sessionName='%s', sessionNumber=%d, memoryUsage=%d, selected=%b}",
                name, pid, sessionName, sessionNumber, memoryUsage, selected.get());
    }
}


