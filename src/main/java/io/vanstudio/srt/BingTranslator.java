package io.vanstudio.srt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URI;

import static io.vanstudio.srt.Languages.isSupport;

public class BingTranslator extends AbstractTranslator {
    /**
     * // Pass secret key and region using headers
     * curl -X POST "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=zh-CN" \
     *      -H "Ocp-Apim-Subscription-Key:<your-key>" \
     *      -H "Ocp-Apim-Subscription-Region:<your-region>" \
     *      -H "Content-Type: application/json" \
     *      -d "[{'Text':'Hello, what is your name?'}]"
     */
    private static final String PATH = "https://api.cognitive.microsofttranslator.com/translate"; //地址: VanStudio
    private final String KEY;
    private final String REGION; 

    private static BingTranslator _instance;

    public static Translator getInstance() {
        if (null == _instance) {
            _instance = new BingTranslator();
        }
        return _instance;
    }

    public BingTranslator() {
        KEY = System.getenv("BING_TRANSLATOR_KEY");
        REGION = System.getenv("BING_TRANSLATOR_REGION");
    }

    @Override
    public String translateText(String text, String sourceLang, String targetLang) throws Exception {
        StringBuilder retStr = new StringBuilder();
        if (!(isSupport(sourceLang) || isSupport(targetLang))) {
            throw new Exception("不支持的语言类型");
        }

        String resp = "";
        try {
            Gson gson = new Gson();
            TranslateRequest[] request = new TranslateRequest[]{ new TranslateRequest(text)};
            String body = gson.toJson(request, new TypeToken<TranslateRequest[]>() {}.getType());

            URI uri = new URIBuilder(PATH)
                    .addParameter("api-version", "3.0")
                    .addParameter("to", targetLang)
                    .build();

            resp = postHttp(uri, body);
//        String resp = "[{\"detectedLanguage\":{\"language\":\"en\",\"score\":1.0},\"translations\":[{\"text\":\"（大管弦乐大张旗鼓\\n播放）\\n\",\"to\":\"zh-Hans\"}]}]";

            /**
             * [{"detectedLanguage":{"language":"en","score":1.0},"translations":[{"text":"（大管弦乐大张旗鼓\n播放）\n","to":"zh-Hans"}]}]
             */
            Type type = new TypeToken<TranslateResult[]>(){}.getType();
            TranslateResult[] translateResult =gson.fromJson(resp, type);
            for (TranslateResult r : translateResult) {
                Translation[] translations = r.translations;
                for (Translation translation : translations) {
                    retStr.append(translation.text);
                }
            }
        } catch (Exception e) {
            StringWriter s = new StringWriter();
            PrintWriter log = new PrintWriter(s);
            e.printStackTrace(log);
            throw new Exception("translateText exception : " + resp + "\n" + s);
        }

        return retStr.toString();
    }

    @Override
    protected void setExtraPostHeader(HttpPost httpPost) {
        httpPost.setHeader("Ocp-Apim-Subscription-Key", KEY);
        httpPost.setHeader("Ocp-Apim-Subscription-Region", REGION);
        httpPost.setHeader("Content-Type", "application/json");
    }

    @Override
    protected void setExtraGetHeader(HttpGet httpGet) {
        httpGet.setHeader("Ocp-Apim-Subscription-Key", KEY);
        httpGet.setHeader("Ocp-Apim-Subscription-Region", REGION);
        httpGet.setHeader("Content-Type", "application/json");
    }

    public static class TranslateResult {
        public DetectedLanguage detectedLanguage;
        public Translation[] translations;
    }

    public static class DetectedLanguage {
        public String language;
        public String score;
    }

    public static class Translation {
        public String text;
        public String to;
    }

    public static class TranslateRequest {
        public String text;

        public TranslateRequest(String text) {
            this.text = text;
        }
    }
}
