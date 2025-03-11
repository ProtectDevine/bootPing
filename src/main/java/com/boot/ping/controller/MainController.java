package com.boot.ping.controller;

import com.boot.ping.MainResponseDto;
import com.boot.ping.service.MainService;
import com.boot.ping.stage.PopupStage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        public void initialize() {
//            String javaVersion = System.getProperty("java.version");
//            String javafxVersion = System.getProperty("javafx.version");
            label.setText("Boost Your Ping");
        }

//        @FXML
//        public void pingCheck() throws IOException {
//
//             MainResponseDto.PingDto responseDto = this.mainService.getPingMessage();
//             label.setText(responseDto.getResponsePing());
//        }

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

        @FXML
        public void pingBoost() throws InvocationTargetException, IllegalAccessException {


//            this.mainService.getNetworkInterfaceGUID();
            this.mainService.runOptimization();



        }





}
