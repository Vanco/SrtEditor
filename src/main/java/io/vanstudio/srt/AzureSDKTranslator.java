package io.vanstudio.srt;

import com.azure.ai.translation.text.TextTranslationClient;
import com.azure.ai.translation.text.TextTranslationClientBuilder;
import com.azure.ai.translation.text.models.TranslateOptions;
import com.azure.ai.translation.text.models.TranslatedTextItem;
import com.azure.ai.translation.text.models.TranslationText;
import com.azure.core.credential.AzureKeyCredential;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AzureSDKTranslator implements Translator{
    public static final String ENDPOINT = "https://api.cognitive.microsofttranslator.com";
    private final TextTranslationClient client;

    private static AzureSDKTranslator _instance;

    public static AzureSDKTranslator getInstance() {
        if (_instance == null) {
            _instance = new AzureSDKTranslator();
        }
        return _instance;
    }

    public AzureSDKTranslator() {
        String key = System.getenv("BING_TRANSLATOR_KEY");
        String region = System.getenv("BING_TRANSLATOR_REGION");

        AzureKeyCredential credential = new AzureKeyCredential(key);
        client = new TextTranslationClientBuilder()
                .credential(credential)
                .region(region)
                .endpoint(ENDPOINT)
                .buildClient();
    }

    @Override
    public String translateText(String text, String sourceLang, String targetLang) throws Exception {
        try {
            TranslatedTextItem translate = client.translate(text, new TranslateOptions().setTargetLanguages(List.of(targetLang)));
            return translate.getTranslations().stream().map(TranslationText::getText).collect(Collectors.joining());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> translateText(List<String> text, String sourceLang, String targetLang) throws Exception {
        try {
            List<TranslatedTextItem> translations = client.translate(text, new TranslateOptions().setTargetLanguages(List.of(targetLang)));
            return translations.stream().map(it -> it.getTranslations().stream().map(TranslationText::getText).collect(Collectors.joining())).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isMultiTranslateSupported() {
        return true;
    }

    @Override
    public void connect() throws Exception {

    }

    @Override
    public void close() throws IOException {

    }
}
