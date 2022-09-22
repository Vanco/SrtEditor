package io.vanstudio.srt;

import java.io.Closeable;
import java.io.IOException;

public interface Translator extends Closeable {
    String translateText(String text, String sourceLang, String targetLang) throws Exception;

    void connect() throws Exception;

    @Override
    void close() throws IOException;
}
