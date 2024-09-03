package io.vanstudio.srt;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface Translator extends Closeable {
    String translateText(String text, String sourceLang, String targetLang) throws Exception;
    List<String> translateText(List<String> text, String sourceLang, String targetLang) throws Exception;
    boolean isMultiTranslateSupported();
    void connect() throws Exception;

    @Override
    void close() throws IOException;
}
