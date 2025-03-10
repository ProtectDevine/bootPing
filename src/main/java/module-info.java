module ping.main {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.desktop;
    requires static lombok;

    opens com.boot.ping to javafx.fxml;
    exports com.boot.ping;
}