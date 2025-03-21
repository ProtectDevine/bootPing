package com.boot.ping.dto;

import lombok.Builder;
import lombok.Getter;

public class CodeDto {

    @Getter
    @Builder
    public static class AlertDto {

        private String title;
        private String description;

    }


}
