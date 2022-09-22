package io.vanstudio.srt;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static io.vanstudio.srt.Languages.isSupport;

public class BingTranslator extends AbstractTranslator {
    private static final String PATH = "http://api.microsofttranslator.com/v2/Http.svc/Translate"; //地址

    private static BingTranslator _instance;

    public static Translator getInstance() {
        if (null == _instance) {
            _instance = new BingTranslator();
        }
        return _instance;
    }

    @Override
    public String translateText(String text, String sourceLang, String targetLang) throws Exception {
        StringBuilder retStr = new StringBuilder();
        if (!(isSupport(sourceLang) || isSupport(targetLang))) {
            throw new Exception("不支持的语言类型");
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appId", "A4D660A48A6A97CCA791C34935E4C02BBB1BEC1C"));
        params.add(new BasicNameValuePair("from", ""));
        params.add(new BasicNameValuePair("to", targetLang));
        params.add(new BasicNameValuePair("text", strip(text)));

        String resp = getHttp(PATH, params);

        String str = strip(resp);
//        String code = StringEscapeUtils.unescapeXml(str);
        retStr.append(str);

        return retStr.toString();
    }

}
