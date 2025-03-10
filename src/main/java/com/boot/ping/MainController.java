package com.boot.ping;

import com.boot.ping.service.MainService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {

        private final MainService mainService = new MainService();

        @FXML
        private Label label;

        public void initialize() {
            String javaVersion = System.getProperty("java.version");
            String javafxVersion = System.getProperty("javafx.version");
            label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
        }

        @FXML
        public void pingCheck() throws IOException {

             MainResponseDto.PingDto responseDto = this.mainService.getPingMessage();
             label.setText(responseDto.getResponsePing());
        }


}
