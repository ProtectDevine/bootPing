package com.boot.ping;

import lombok.Builder;
import lombok.Getter;

public class MainResponseDto {

    @Builder
    @Getter
    public static class PingDto {
        private String responsePing;
    }

}
