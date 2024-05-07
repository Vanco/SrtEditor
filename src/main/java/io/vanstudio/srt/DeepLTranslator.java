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

public class DeepLTranslator extends AbstractTranslator {
    /**
     * // Pass secret key and region using headers
     * curl -X POST http://localhost:1188/translate \
     * -H "Content-Type: application/json" \
     * -d '{
     *     "text": "Hello, world!",
     *     "source_lang": "EN",
     *     "target_lang": "ZH"
     * }'
     */
    private static final String PATH = "http://localhost:1188/translate"; //地址: VanStudio

    private static DeepLTranslator _instance;

    public static Translator getInstance() {
        if (null == _instance) {
            _instance = new DeepLTranslator();
        }
        return _instance;
    }

    public DeepLTranslator() {
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
            TranslateRequest request = new TranslateRequest(text, sourceLang, targetLang.length() > 2 ? targetLang.substring(0,2): sourceLang);
            String body = gson.toJson(request, new TypeToken<TranslateRequest>() {}.getType());

            URI uri = new URIBuilder(PATH)
                    .build();

            resp = postHttp(uri, body);
//            resp = "{\"alternatives\":[\"世界，你好\",\"你好，世界！\",\"大家好\"],\"code\":200,\"data\":\"你好，世界\",\"id\":8348254001,\"method\":\"Free\",\"source_lang\":\"NL\",\"target_lang\":\"ZH\"}";
//            resp = "{\"code\":406,\"message\":\"Invalid target language\"}";

            Type type = new TypeToken<TranslateResult>(){}.getType();
            TranslateResult translateResult =gson.fromJson(resp, type);
            if (translateResult.code == 200) {
                retStr.append(translateResult.data);
            } else {
                throw new Exception("translateText exception : " + resp + "\n");
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
        httpPost.setHeader("Content-Type", "application/json");
    }

    @Override
    protected void setExtraGetHeader(HttpGet httpGet) {
        httpGet.setHeader("Content-Type", "application/json");
    }

    /**
     * {"alternatives":["世界，你好","你好，世界！","大家好"],"code":200,"data":"你好，世界","id":8348254001,"method":"Free","source_lang":"NL","target_lang":"ZH"}
     */
    public static class TranslateResult{
        public String[] alternatives;
        public int code;
        public String data;
        public long id;
        public String method;
        public String source_lang;
        public String target_lang;
        public String message;
    }

    public static class TranslateRequest {
        public String text;
        public String source_lang;
        public String target_lang;

        public TranslateRequest(String text, String from, String to) {
            this.text = text;
            this.source_lang = from;
            this.target_lang = to;
        }
    }

}
