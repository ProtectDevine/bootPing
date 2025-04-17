package com.boot.ping.controller;

import com.boot.ping.MainResponseDto;
import com.boot.ping.dto.TaskDto;
import com.boot.ping.enums.MenuCodes;
import com.boot.ping.service.MainService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
        public void getTasks() throws IOException {
            TableView<TaskDto> table = new TableView<>();
            List<TaskDto> tasks = this.mainService.getTasks();

            TableColumn<TaskDto, String> imageNameCol = new TableColumn<>(MenuCodes.menuDisplay(MenuCodes.TASK_NAME).getMenu());
            imageNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<TaskDto, Integer> pidCol = new TableColumn<>(MenuCodes.menuDisplay(MenuCodes.TASK_PID).getMenu());
            pidCol.setCellValueFactory(new PropertyValueFactory<>("pid"));

            TableColumn<TaskDto, String> sessionNameCol = new TableColumn<>(MenuCodes.menuDisplay(MenuCodes.TASK_SESSION_NAME).getMenu());
            sessionNameCol.setCellValueFactory(new PropertyValueFactory<>("sessionName"));

            TableColumn<TaskDto, Integer> sessionNumberCol = new TableColumn<>(MenuCodes.menuDisplay(MenuCodes.TASK_SESSION_NUMBER).getMenu());
            sessionNumberCol.setCellValueFactory(new PropertyValueFactory<>("sessionNumber"));

            TableColumn<TaskDto, Long> memoryUsageCol = new TableColumn<>(MenuCodes.menuDisplay(MenuCodes.TASK_MEMORY_USAGE).getMenu());
            memoryUsageCol.setCellValueFactory(new PropertyValueFactory<>("memoryUsage"));

            table.getColumns().addAll(imageNameCol, pidCol, sessionNameCol, sessionNumberCol, memoryUsageCol);
            table.getItems().addAll(tasks);

            VBox root = new VBox(table);
            Scene scene = new Scene(root, 640, 480);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }




}
