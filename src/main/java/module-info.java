module io.vanstudio.srt {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jdk.charsets;
    requires com.azure.ai.translation.text;
    requires com.azure.core;
    requires com.azure.http.netty;
    requires com.azure.json;
    requires com.azure.xml;
    requires io.netty.transport;
    requires io.netty.handler;
    requires io.netty.handler.proxy;
    requires io.netty.codec.http;

    opens io.vanstudio.srt to javafx.fxml;
    exports io.vanstudio.srt;
}