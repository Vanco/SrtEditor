//package io.vanstudio.srt;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.message.BasicNameValuePair;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static io.vanstudio.srt.Languages.isSupport;
//
///**
// * @author aping
// * @time 2020/5/22 16:42
// */
//public class GoogleTranslator extends AbstractTranslator {
//
//
//    private static final String PATH = "https://translate.googleapis.com/translate_a/single"; //地址
//    private static final String CLIENT = "gtx";
//
//
//    private static Translator _instance = null;
//
//    /**
//     * 获取单例
//     *
//     * @return
//     */
//    public static Translator getInstance() {
//        if (null == _instance) {
//            _instance = new GoogleTranslator();
//        }
//        return _instance;
//    }
//
//    /**
//     * 翻译文本
//     *
//     * @param text       文本内容
//     * @param sourceLang 文本所属语言。如果不知道，可以使用auto
//     * @param targetLang 目标语言。必须是明确的有效的目标语言
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public String translateText(String text, String sourceLang, String targetLang) throws Exception {
//
//
//        StringBuilder retStr = new StringBuilder();
//        if (!(isSupport(sourceLang) || isSupport(targetLang))) {
//            throw new Exception("不支持的语言类型");
//        }
//
//        List<NameValuePair> nvps = new ArrayList<>();
//        nvps.add(new BasicNameValuePair("client", CLIENT));
//        nvps.add(new BasicNameValuePair("sl", sourceLang));
//        nvps.add(new BasicNameValuePair("tl", targetLang));
//        nvps.add(new BasicNameValuePair("dt", "t"));
//        nvps.add(new BasicNameValuePair("q", text));
////        String finalPath=PATH +"?client="+CLIENT+"&sl="+sourceLang+"&tl="+targetLang+"&dt=t&q="+ text ;
//
//        String resp = postHttp(PATH, nvps);
//        if (null == resp) {
//            throw new Exception("网络异常");
//        }
//
////        System.out.println( "==>返回内容：" + resp);
//
//        try {
//            JsonElement json = JsonParser.parseString(resp);
//            JsonArray jsonObject = json.getAsJsonArray();
//            for (JsonElement o : jsonObject.get(0).getAsJsonArray()) {
//                JsonArray a = o.getAsJsonArray();
//                retStr.append(a.get(0).getAsString());
//            }
////
////            JSONArray jsonObject = JSONArray.parseArray(resp);
////            for (Object o : jsonObject.getJSONArray(0)) {
////                JSONArray a = (JSONArray) o;
////                retStr += a.getString(0);
////            }
//        } catch (Exception e) {
//            StringWriter s = new StringWriter();
//            PrintWriter log = new PrintWriter(s);
//            e.printStackTrace(log);
//            throw new Exception("translateText exception : " + resp + "\n" + s);
//            //retStr = text;
//        }
//
//        return retStr.toString();
//    }
//
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
//
//
//
//
//
//
//
//
//
//
//
