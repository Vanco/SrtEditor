package io.vanstudio.srt;

public class TranslatorFactory {
    public static Translator getInstance(String provider) {
        if ("llm".equals(provider)) return LLMTranslator.getInstance();
        if ("google".equals(provider)) return AzureSDKTranslator.getInstance();
        if ("bing".equals(provider)) return AzureSDKTranslator.getInstance();
        if ("youdao".equals(provider)) return AzureSDKTranslator.getInstance();
        if ("deepl".equals(provider)) return AzureSDKTranslator.getInstance();
        return AzureSDKTranslator.getInstance();
    }

    public static Translator getInstance() {
        return AzureSDKTranslator.getInstance();
    }
}
