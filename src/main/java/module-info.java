module io.vanstudio.srt {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jdk.charsets;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    requires com.google.gson;

    opens io.vanstudio.srt to javafx.fxml;
    exports io.vanstudio.srt;
}