module io.vanstudio.srt {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jdk.charsets;
    requires com.azure.ai.translation.text;
    requires context.propagation;
    requires reactor.blockhound;
    requires com.azure.core;
    requires com.azure.http.netty;
    requires com.azure.json;
    requires com.azure.xml;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.handler.proxy;
    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.codec.http;
    requires io.netty.codec.http2;
    requires io.netty.tcnative.classes.openssl;

    opens io.vanstudio.srt to javafx.fxml;
    exports io.vanstudio.srt;
}