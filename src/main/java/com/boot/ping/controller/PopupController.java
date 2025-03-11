package com.boot.ping.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PopupController {

    @FXML
    private Label responseLabel;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setResponseText(String response) {
        responseLabel.setText(response);
    }

    @FXML
    private void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }
}
