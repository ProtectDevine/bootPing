package com.boot.ping.controller;

import com.boot.ping.MainResponseDto;
import com.boot.ping.service.MainService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        private Button boostButton;

        private String guid;

        public void initialize() {

            this.label.setText("Boost Your Ping");

            this.guid = this.mainService.getGUID();

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
            this.boostButton.setText("Boost Status : " + hasTcpNoDelay);

            if (hasTcpNoDelay) {
                boostButton.setStyle("-fx-text-fill: green;");
            } else {
                boostButton.setStyle("-fx-text-fill: red;");
            }

        }





}
