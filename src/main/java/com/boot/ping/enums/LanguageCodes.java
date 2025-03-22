package com.boot.ping.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LanguageCodes {

    KOREAN("ko"),
    ENGLISH("en");

    private final String language;

    public static LanguageCodes getLanguageCode(String language) {

        return Arrays.stream(LanguageCodes.values())
                .filter(x -> x.getLanguage()
                .equals(language)).findFirst()
                .orElseThrow(() -> new RuntimeException());

    }



}
