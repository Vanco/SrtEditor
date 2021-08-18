module io.vanstudio.srt {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jdk.charsets;

    opens io.vanstudio.srt to javafx.fxml;
    exports io.vanstudio.srt;
}