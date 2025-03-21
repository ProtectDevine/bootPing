package com.boot.ping.enums;

import com.boot.ping.dto.CodeDto;
import com.boot.ping.utils.LocaleUtil;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AlertCodes {

    PING_CHECK_FAIL(
            "오류 발생", "네트워크 연결을 찾지 못했습니다.",
    "ERROR", "Network connection not found."),


    GUID_CHECK_FAIL(
            "오류 발생", "GUID를 찾지 못했습니다.",
            "ERROR", "GUID not found."),


    BOOST_PING_COMPLETE(
            "완료", "네트워크 최적화 설정이 완료되었습니다.",
            "COMPLETE", "Network optimization settings are complete."
            ),
    BOOST_PING_FAIL(
            "오류 발생", "네트워크 최적화 실행 중 오류가 발생했습니다.",
            "ERROR", "Error running network optimization."
    )
    ;

    private final String title;
    private final String description;
    private final String titleEn;
    private final String descriptionEn;

    public static CodeDto.AlertDto getAlertCodes(AlertCodes alertCodes, String language) {

        LanguageCodes languageCodes =
                Arrays.stream(LanguageCodes.values())
                        .filter(x -> language.equals(x.getLanguage()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("can't find language"));

        return switch (languageCodes.getLanguage()) {
            case "ko" -> CodeDto.AlertDto.builder()
                    .title(alertCodes.title)
                    .description(alertCodes.description)
                    .build();
            default -> CodeDto.AlertDto.builder()
                    .title(alertCodes.titleEn)
                    .description(alertCodes.getDescriptionEn())
                    .build();
        };

    }

    public static String alertDisplay(AlertCodes alertCodes) {
        LocaleUtil localeUtil = new LocaleUtil();
        CodeDto.AlertDto alert = AlertCodes.getAlertCodes(alertCodes, localeUtil.getUserLocale());
        showAlert(alert.getTitle(), alert.getDescription());
        return alert.getDescription();
    }

    public static String alertDisplay(AlertCodes alertCodes, String exceptionMessage) {
        LocaleUtil localeUtil = new LocaleUtil();
        CodeDto.AlertDto alert = AlertCodes.getAlertCodes(alertCodes, localeUtil.getUserLocale());
        showAlert(alert.getTitle(), alert.getDescription() + "\n" + exceptionMessage);
        return alert.getDescription();
    }


    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



}
