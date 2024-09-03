//package io.vanstudio.srt;
//
////import com.google.gson.JsonArray;
////import com.google.gson.JsonElement;
////import com.google.gson.JsonObject;
////import com.google.gson.JsonParser;
////import org.apache.http.NameValuePair;
////import org.apache.http.client.methods.HttpGet;
////import org.apache.http.client.methods.HttpPost;
////import org.apache.http.message.BasicNameValuePair;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static io.vanstudio.srt.Languages.isSupport;
//
//public class YoudaoTranslator extends AbstractTranslator{
//
//    private static final String PATH = "http://fanyi.youdao.com/translate"; //地址
//
//    private static YoudaoTranslator instance;
//    public static Translator getInstance() {
//        if (null == instance) {
//            instance = new YoudaoTranslator();
//        }
//        return instance;
//    }
//
//    @Override
//    public String translateText(String text, String sourceLang, String targetLang) throws Exception {
//
//        StringBuilder retStr = new StringBuilder();
//        if (!(isSupport(sourceLang) || isSupport(targetLang))) {
//            throw new Exception("不支持的语言类型");
//        }
//
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("doctype", "json"));
//        params.add(new BasicNameValuePair("type", "EN2ZH_CN"));
//        params.add(new BasicNameValuePair("i", text));
//
//        String resp = getHttp(PATH, params);
//
//        try {
//            JsonObject obj = JsonParser.parseString(resp).getAsJsonObject();
//            JsonArray result = obj.getAsJsonArray("translateResult");
//            for (JsonElement jsonElement : result) {
//                for (JsonElement element : jsonElement.getAsJsonArray()) {
//                    String tgt = element.getAsJsonObject().get("tgt").getAsString();
//                    retStr.append(tgt);
//                }
//            }
//
//        } catch (Exception e) {
//            StringWriter s = new StringWriter();
//            PrintWriter log = new PrintWriter(s);
//            e.printStackTrace(log);
//            throw new Exception("translateText exception : " + resp + "\n" + s);
//        }
//
//        return retStr.toString();
//    }
//
//    @Override
//    protected void setExtraPostHeader(HttpPost httpPost) {
//
//    }
//
//    @Override
//    protected void setExtraGetHeader(HttpGet httpGet) {
//
//    }
//}
