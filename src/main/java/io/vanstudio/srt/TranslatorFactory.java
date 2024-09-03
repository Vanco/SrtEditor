package io.vanstudio.srt;

public class TranslatorFactory {
    public static Translator getInstance(String provider) {
        if ("google".equals(provider)) return AzureSDKTranslator.getInstance(); // GoogleTranslator.getInstance();
        if ("bing".equals(provider)) return AzureSDKTranslator.getInstance(); // BingTranslator.getInstance();
        if ("youdao".equals(provider)) return AzureSDKTranslator.getInstance(); // YoudaoTranslator.getInstance();
        if ("deepl".equals(provider)) return AzureSDKTranslator.getInstance(); // DeepLTranslator.getInstance();
        return AzureSDKTranslator.getInstance();
    }
}
