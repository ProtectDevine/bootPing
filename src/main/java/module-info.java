module ping.main {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.desktop;
    requires static lombok;
    requires java.prefs;

    opens com.boot.ping to javafx.fxml;
    exports com.boot.ping;
    exports com.boot.ping.controller;
    opens com.boot.ping.controller to javafx.fxml;
}