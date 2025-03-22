package com.boot.ping;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
    public void start(Stage stage) throws Exception {

        // debug 시 if문 주석
        if (!isRunningAsAdmin()) {
            tryRunAsAdmin(stage);
            return; // 재실행 시도 후 현재 프로세스 종료 안 함
        }

        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("Boot Ping");
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/boot/ping/bootping_logo.png")));
        stage.setResizable(false);
        stage.show();

    }

    private boolean isRunningAsAdmin() {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "net session >nul 2>&1");
            Process process = builder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            System.err.println("관리자 권한 체크 실패: " + e.getMessage());
            return false;
        }
    }

    private void tryRunAsAdmin(Stage stage) {
        try {
            File currentFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String appPath = currentFile.getAbsolutePath();
            System.out.println("현재 실행 파일 경로: " + appPath);

            String command;
            if (appPath.endsWith(".jar")) {
                System.out.println("JAR 환경 감지");
                if (!new File(appPath).exists()) {
                    System.out.println("JAR 파일이 존재하지 않습니다: " + appPath);
                    appPath = new File("build/libs/ping-1.0-SNAPSHOT.jar").getAbsolutePath();
                }
                if (!new File(appPath).exists()) {
                    System.out.println("기본 JAR 파일도 존재하지 않습니다: " + appPath);
                    showBasicUI(stage);
                    return;
                }
                //배포
                String javaCmd = ".\\jre\\bin\\java --module-path \".\\jre\\lib\" --add-modules javafx.controls,javafx.fxml -jar \"" + appPath + "\"";                //로컬
                //로컬
//                String javaCmd = "java --module-path \"C:\\javafx-sdk-17.0.14\\lib\" --add-modules javafx.controls,javafx.fxml -jar \"" + appPath + "\"";
                command = "powershell -Command \"Start-Process cmd -ArgumentList '/c " + javaCmd + "' -Verb RunAs -WindowStyle Hidden\"";
                System.out.println("JAR 실행 명령어: " + command);
            } else if (appPath.endsWith(".exe")) {
                System.out.println("EXE 환경 감지");
                String exePath = System.getProperty("user.dir") + File.separator + "BootPing.exe";
                if (!new File(exePath).exists()) {
                    System.out.println("EXE 파일이 존재하지 않습니다: " + exePath);
                    showBasicUI(stage);
                    return;
                }
                command = "powershell -Command \"Start-Process \\\"" + exePath + "\\\" -Verb RunAs -WindowStyle Hidden\"";
                System.out.println("EXE 실행 명령어: " + command);
            } else {
                System.out.println("IDE 클래스 환경 감지");
                appPath = new File("build/libs/ping-1.0-SNAPSHOT.jar").getAbsolutePath();
                if (!new File(appPath).exists()) {
                    System.out.println("JAR 파일이 존재하지 않습니다: " + appPath);
                    showBasicUI(stage);
                    return;
                }
                //배포
                String javaCmd = ".\\jre\\bin\\java --module-path \".\\jre\\lib\" --add-modules javafx.controls,javafx.fxml -jar \"" + appPath + "\"";
                //로컬
//                String javaCmd = "java --module-path \"C:\\javafx-sdk-17.0.14\\lib\" --add-modules javafx.controls,javafx.fxml -jar \"" + appPath + "\"";
                command = "powershell -Command \"Start-Process cmd -ArgumentList '/c " + javaCmd + "' -Verb RunAs -WindowStyle Hidden\"";
                System.out.println("JAR 실행 명령어: " + command);
            }

            executeCommand(command);
            stage.close();
            System.exit(0);
        } catch (Exception e) {
            System.err.println("재실행 실패: " + e.getMessage());
            e.printStackTrace();
            showBasicUI(stage);
        }
    }

    private void executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS949"));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("출력: " + line);
        }
        int exitCode = process.waitFor();
        System.out.println("명령어 종료 코드: " + exitCode);
    }

    private void showBasicUI(Stage stage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);
        stage.setTitle("Ping App (관리자 권한 필요)");
        stage.setScene(scene);
        stage.show();
    }

}