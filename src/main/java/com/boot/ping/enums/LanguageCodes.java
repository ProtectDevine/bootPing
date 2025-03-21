package com.boot.ping.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LanguageCodes {

    KOREAN("ko"),
    ENGLISH("en");

    private final String language;

}
