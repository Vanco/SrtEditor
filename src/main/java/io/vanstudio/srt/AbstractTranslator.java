package io.vanstudio.srt;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public abstract class AbstractTranslator implements Translator {
    private static final String USER_AGENT = "Mozilla/5.0";//"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";

    protected CloseableHttpClient httpClient;
    protected boolean connected = false;

    @Override
    public void connect() throws Exception {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .setProtocol("TLSv1.2")
                    .loadTrustMaterial(null, new AnyTrustStrategy())
                    .build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
            httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
//                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .build();
            connected = true;
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            StringWriter s = new StringWriter();
            PrintWriter log = new PrintWriter(s);
            e.printStackTrace(log);
            throw new Exception("Connect exception :" + s);
        }
    }

    @Override
    public void close() throws IOException {
        if (httpClient != null)
            httpClient.close();
    }

    /**
     * post 请求
     *
     * @param url  请求地址
     * @param nvps 参数列表
     * @return
     * @throws
     */
    protected String postHttp(String url, List<NameValuePair> nvps) throws Exception {
        String responseStr = "";
        HttpPost httpPost = new HttpPost(url);
        //重要！！必须设置 http 头，否则返回为乱码
        httpPost.setHeader("User-Agent", USER_AGENT);

        // 重要！！ 指定编码，对中文进行编码
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response2 = httpClient.execute(httpPost)) {
            HttpEntity entity2 = response2.getEntity();
            responseStr = EntityUtils.toString(entity2);
            EntityUtils.consume(entity2);
        } catch (IOException | ParseException e) {
            StringWriter s = new StringWriter();
            PrintWriter log = new PrintWriter(s);
            e.printStackTrace(log);
            throw new Exception("PostHttp exception " + s);
        }
        return responseStr;
    }

    protected String getHttp(String url, List<NameValuePair> params) throws Exception {
        String responseStr = "";
        URI uri = new URIBuilder(url).addParameters(params).build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("User-Agent", USER_AGENT);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            responseStr = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            StringWriter s = new StringWriter();
            PrintWriter log = new PrintWriter(s);
            e.printStackTrace(log);
            throw new Exception("PostHttp exception " + s);
        }
        return responseStr;
    }

    /**
     * Remove all xml tag in string.
     * e.g.  <i>good</i> => good
     * @param xml source String.
     * @return string
     */
    protected String strip(String xml) {
        return xml.replaceAll("<[^>]+>", "");
    }
}
