package io.vanstudio.srt;

public class TranslatorFactory {
    public static Translator getInstance(String provider) {
        if ("google".equals(provider)) return GoogleTranslator.getInstance();
        if ("bing".equals(provider)) return BingTranslator.getInstance();
        if ("youdao".equals(provider)) return YoudaoTranslator.getInstance();
        return GoogleTranslator.getInstance();
    }
}
