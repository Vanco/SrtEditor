//package io.vanstudio.srt;
//
//import reactor.netty.http.client.HttpClient;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//public abstract class AbstractTranslator implements Translator {
//    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.50";//"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";
//
//    protected HttpClient httpClient;
//    protected boolean connected = false;
//
//    @Override
//    public List<String> translateText(List<String> text, String sourceLang, String targetLang) throws Exception {
//        throw new Exception("Multi-translate is not supported");
//    }
//
//    @Override
//    public boolean isMultiTranslateSupported() {
//        return false;
//    }
//
//    @Override
//    public void connect() throws Exception {
////        try {
////            httpClient = HttpClient.create()
////                    .secure();
////            connected = true;
////        } catch (Exception e) {
////            StringWriter s = new StringWriter();
////            PrintWriter log = new PrintWriter(s);
////            e.printStackTrace(log);
////            throw new Exception("Connect exception :" + s);
////        }
//    }
//
//    @Override
//    public void close() throws IOException {
////        if (httpClient != null)
////            httpClient.cache().close();
//    }
//
//    /**
//     * post 请求
//     *
//     * @param url  请求地址
//     * @param nvps 参数列表
//     * @return
//     * @throws
//     */
//    protected String postHttp(String url, List<NameValuePair> nvps) throws Exception {
//        String responseStr = "";
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8);
//        responseStr = doPost(new URI(url), entity);
//        return responseStr;
//    }
//
//    protected String postHttp(String url, String body) throws Exception {
//        return postHttp(new URI(url), body);
//    }
//    protected String postHttp(URI uri, String body) throws Exception {
//        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
//        return doPost(uri, entity);
//    }
//
//    private String doPost(URI url, StringEntity entity) throws Exception {
//        String responseStr;
//        HttpPost httpPost = new HttpPost(url);
//        //重要！！必须设置 http 头，否则返回为乱码
//        httpPost.setHeader("User-Agent", USER_AGENT);
//        setExtraPostHeader(httpPost);
//
//        // 重要！！ 指定编码，对中文进行编码
//        httpPost.setEntity(entity);
//
//        try (CloseableHttpResponse response2 = httpClient.execute(httpPost)) {
//            HttpEntity entity2 = response2.getEntity();
//            responseStr = EntityUtils.toString(entity2);
//            EntityUtils.consume(entity2);
//        } catch (IOException | ParseException e) {
//            StringWriter s = new StringWriter();
//            PrintWriter log = new PrintWriter(s);
//            e.printStackTrace(log);
//            throw new Exception("PostHttp exception " + s);
//        }
//        return responseStr;
//    }
//
//    protected String getHttp(String url, List<NameValuePair> params) throws Exception {
//        String responseStr = "";
//        URI uri = new URIBuilder(url).addParameters(params).build();
//        HttpGet httpGet = new HttpGet(uri);
//        httpGet.setHeader("User-Agent", USER_AGENT);
//        setExtraGetHeader(httpGet);
//
//        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
//            HttpEntity entity = response.getEntity();
//            responseStr = EntityUtils.toString(entity);
//            EntityUtils.consume(entity);
//        } catch (IOException e) {
//            StringWriter s = new StringWriter();
//            PrintWriter log = new PrintWriter(s);
//            e.printStackTrace(log);
//            throw new Exception("PostHttp exception " + s);
//        }
//        return responseStr;
//    }
//
//    protected abstract void setExtraPostHeader(HttpPost httpPost);
//    protected abstract void setExtraGetHeader(HttpGet httpGet);
//
//    /**
//     * Remove all xml tag in string.
//     * e.g.  <i>good</i> => good
//     * @param xml source String.
//     * @return string
//     */
//    protected String strip(String xml) {
//        return xml.replaceAll("<[^>]+>", "");
//    }
//}
