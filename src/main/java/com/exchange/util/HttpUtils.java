package com.exchange.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by GYH on 2022/11/16
 */
public class HttpUtils {

    /**
     * get
     */
    public static String doGet(String host, String path,
                               Map<String, String> headers,
                               Map<String, String> querys) throws Exception {
        CloseableHttpClient httpClient = wrapClient(host);

        HttpGet request = new HttpGet(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        return httpClient.execute(request, new StringHttpClientResponseHandler());
    }

    /**
     * post form
     */
    public static String doPost(String host, String path,
                                               Map<String, String> headers,
                                               Map<String, String> querys,
                                               Map<String, String> bodys) throws Exception {
        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, StandardCharsets.UTF_8);
            request.setEntity(formEntity);
        }

        return httpClient.execute(request, new StringHttpClientResponseHandler());
    }

    /**
     * Post String
     */
    public static CloseableHttpResponse doPost(String host, String path,
                                               Map<String, String> headers,
                                               Map<String, String> querys,
                                               String body) throws Exception {
        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        }

        return httpClient.execute(request);
    }

    /**
     * Post stream
     */
    public static CloseableHttpResponse doPost(String host, String path,
                                               Map<String, String> headers,
                                               Map<String, String> querys,
                                               byte[] body) throws Exception {
        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body, ContentType.DEFAULT_BINARY));
        }

        return httpClient.execute(request);
    }

    /**
     * Put String
     */
    public static CloseableHttpResponse doPut(String host, String path,
                                              Map<String, String> headers,
                                              Map<String, String> querys,
                                              String body) throws Exception {
        CloseableHttpClient httpClient = wrapClient(host);

        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        }

        return httpClient.execute(request);
    }

    /**
     * Put stream
     */
    public static CloseableHttpResponse doPut(String host, String path,
                                              Map<String, String> headers,
                                              Map<String, String> querys,
                                              byte[] body) throws Exception {
        CloseableHttpClient httpClient = wrapClient(host);

        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body, ContentType.DEFAULT_BINARY));
        }

        return httpClient.execute(request);
    }

    /**
     * Delete
     */
    public static CloseableHttpResponse doDelete(String host, String path,
                                                 Map<String, String> headers,
                                                 Map<String, String> querys) throws Exception {
        CloseableHttpClient httpClient = wrapClient(host);

        HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        return httpClient.execute(request);
    }

    private static String buildUrl(String host, String path, Map<String, String> querys) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), StandardCharsets.UTF_8));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    private static CloseableHttpClient wrapClient(String host) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        if (host.startsWith("https://")) {
            return sslClient();
        }

        return httpClient;
    }

    private static CloseableHttpClient sslClient() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] xcs, String str) {

                }

                public void checkServerTrusted(X509Certificate[] xcs, String str) {

                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLConnectionSocketFactory s = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", s)
                    .build();
            HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            return HttpClients.custom().setConnectionManager(cm).build();
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
