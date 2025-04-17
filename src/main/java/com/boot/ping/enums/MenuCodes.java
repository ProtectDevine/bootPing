package com.boot.ping.enums;

import com.boot.ping.dto.CodeDto;
import com.boot.ping.utils.LocaleUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuCodes {


    MAIN_PING_CHECK("핑 확인", "Ping Check"),
    MAIN_PING_BOOST("부스트 상태", "Boost Status"),
    MAIN_KILL_TASK("응용프로그램 끄기", "Kill Task"),
    MAIN_AVERAGE_PING("평균", "Average"),


    TASK_NAME("응용 프로그램", "Program"),
    TASK_PID("PID", "PID"),
    TASK_SESSION_NAME("세션 이름", "Session Name"),
    TASK_SESSION_NUMBER("세션 번호", "Session Number"),
    TASK_MEMORY_USAGE("메모리 사용량", "Memory Usage")
    ;


    private final String menu;
    private final String menuEn;

    public static CodeDto.MenuDto getMenuCodes(MenuCodes menuCodes, String language) {

        return switch (LanguageCodes.getLanguageCode(language)) {
            case KOREAN -> CodeDto.MenuDto.builder()
                    .menu(menuCodes.menu)
                    .build();

            default -> CodeDto.MenuDto.builder()
                    .menu(menuCodes.menuEn)
                    .build();
        };

    }

    public static CodeDto.MenuDto menuDisplay(MenuCodes menuCodes) {
        LocaleUtil localeUtil = new LocaleUtil();
        return MenuCodes.getMenuCodes(menuCodes, localeUtil.getUserLocale());
    }



}
