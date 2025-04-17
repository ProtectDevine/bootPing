package com.boot.ping.controller;

import com.boot.ping.MainResponseDto;
import com.boot.ping.enums.MenuCodes;
import com.boot.ping.service.MainService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MainController {

        private final MainService mainService = new MainService();

        @FXML
        private Label label;
        @FXML
        private Button pingCheckButton;
        @FXML
        private Button boostButton;
        @FXML
        private Button taskButton;



        private String guid;

        public void initialize() {

            this.label.setText("Boost Your Ping");

            this.guid = this.mainService.getGUID();

            this.pingCheckButton.setText(MenuCodes.menuDisplay(MenuCodes.MAIN_PING_CHECK).getMenu());
            this.taskButton.setText(MenuCodes.menuDisplay(MenuCodes.MAIN_KILL_TASK).getMenu());

            boolean hasTcpNoDelay = this.mainService.checkTcpNoDelay(this.guid);
            boostButtonText(hasTcpNoDelay);

        }

        @FXML
        public void pingBoost() throws InvocationTargetException, IllegalAccessException {

            boolean hasTcpNoDelay = (boolean) this.boostButton.getUserData();
            boolean completedOptimization = this.mainService.runOptimization(this.guid, hasTcpNoDelay);

            boostButtonText(completedOptimization);

        }

        @FXML
        public void pingCheck() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/boot/ping/popup.fxml"));
            Parent root = loader.load();

            PopupController popupController = loader.getController();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(root));

            // 팝업 컨트롤러에 데이터 설정
            popupController.setStage(popupStage);

            MainResponseDto.PingDto responseDto = this.mainService.getPingMessage();

            popupController.setResponseText(responseDto.getResponsePing());

            // 팝업 창 표시
            popupStage.showAndWait();
        }

        public void boostButtonText(boolean hasTcpNoDelay) {

            this.boostButton.setUserData(hasTcpNoDelay);
            this.boostButton.setText(MenuCodes.menuDisplay(MenuCodes.MAIN_PING_BOOST).getMenu() + " : " + hasTcpNoDelay);

            if (hasTcpNoDelay) {
                boostButton.setStyle("-fx-text-fill: green;");
            } else {
                boostButton.setStyle("-fx-text-fill: red;");
            }

        }

    @FXML
    public void getTasks() {
        try {
            // tableView.fxml 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/boot/ping/tableView.fxml"));
            VBox root = loader.load();

            // 새로운 창 생성
            Stage stage = new Stage();
            stage.setTitle("Task Manager");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("TableView 창 로드 실패: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("창 로드 실패");
            alert.setContentText("태스크 관리 창을 열지 못했습니다: " + e.getMessage());
            alert.showAndWait();
        }
    }



}
