package com.boot.ping.stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PopupStage extends Stage {

    public PopupStage() throws IOException {

        Parent root = this.getParent();

        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(new Scene(root));
    }

    public FXMLLoader getLoader() {
        return new FXMLLoader(getClass().getResource("/com/boot/ping/popup.fxml"));
    }

    public Parent getParent() throws IOException {
        return this.getLoader().load();
    }



}
