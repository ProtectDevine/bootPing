package com.boot.ping;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {


        @FXML
        private Label label;

        public void initialize() {
            String javaVersion = System.getProperty("java.version");
            String javafxVersion = System.getProperty("javafx.version");
            label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
        }

}
