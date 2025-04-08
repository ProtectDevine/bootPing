package com.boot.ping.dto;

import lombok.Builder;

@Builder
public class RegistryCommandDto {

    private String path;
    private String valueName;
    @Builder.Default private final String type = "REG_QWORD";
    private final long data;

    private String dataAsHex() {
        return type.equals("REG_DWORD") ? String.format("0x%08X", data) : String.valueOf(data);
    }

    public String toCommand() {
        return String.format("reg add \"%s\" /v %s /t %s /d %s /f",
                path, valueName, type, dataAsHex());
    }

}
