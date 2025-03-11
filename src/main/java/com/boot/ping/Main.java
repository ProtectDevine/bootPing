package com.boot.ping;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        checkAndRunAsAdmin();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("Boot Ping");
        stage.setScene(scene);
        stage.show();

    }

    private void checkAndRunAsAdmin() {
        String os = System.getProperty("os.name").toLowerCase();

        // Windows 운영체제인지 확인
        if (!os.contains("win")) {
            return;
        }

        // 관리자 권한이 있는지 확인
        try {
            Process process = Runtime.getRuntime().exec("net session");
            process.waitFor();
            if (process.exitValue() == 0) {
                System.out.println("이미 관리자 권한으로 실행 중");
                return; // 이미 관리자 권한이면 실행하지 않음
            }
        } catch (Exception e) {
            System.out.println("관리자 권한 없음, 재실행 시도...");
        }

        // 현재 실행 중인 JAR 파일 경로 가져오기
        String jarPath = getJarPath();

        if (jarPath == null) {
            System.out.println("JAR 파일 경로를 찾을 수 없습니다.");
            return;
        }

        // 관리자 권한으로 재실행
        try {
            String command = "powershell -Command \"Start-Process java -ArgumentList '-jar \"" + jarPath + "\"' -Verb RunAs\"";
            Runtime.getRuntime().exec(command);
            System.exit(0); // 현재 프로세스 종료 후 관리자 권한으로 실행
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 현재 실행 중인 JAR 파일의 경로를 가져오는 메서드
    private String getJarPath() {
        try {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}