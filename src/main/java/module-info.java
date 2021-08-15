module io.vanstudio.srt {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens io.vanstudio.srt to javafx.fxml;
    exports io.vanstudio.srt;
}