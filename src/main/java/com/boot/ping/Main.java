package com.boot.ping;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
            // 실제 JAR 파일 경로 (빌드 도구에 따라 확인 필요)
            String jarPath = "C:\\workspace\\ping\\build\\libs\\ping-1.0-SNAPSHOT.jar"; // 예시 경로
            // PowerShell 명령어 구성
            String command = "powershell -Command \"Start-Process java -ArgumentList '-jar \\\"" + jarPath + "\\\"' -Verb RunAs\"";
            System.out.println("실행 명령어: " + command);

            // 명령어 실행
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 출력 확인
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



}