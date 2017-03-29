package com.fighter.rocket.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by hanwm on 16/2/24.
 */
public class HttpProxy {

    private String url;
    private HttpClient httpClient;
    private HttpMethod httpMethod;
    private String response;

    public HttpProxy(String url) {
        this.url = url;
        httpClient = new HttpClient();
    }

    public String doGet(Map<String, String> queryMap, Map<String, String> headers)
            throws IOException {
        httpMethod = new GetMethod(url);

        setQueryString(httpMethod, queryMap);
        setHeaders(httpMethod, headers);

        httpClient.executeMethod(httpMethod);

        response = readRespData(httpMethod);
        return response;
    }

    @SuppressWarnings("deprecation")
    public String doPost(Map<String, String> queryMap, Map<String, String> headers, String body) throws IOException {
        PostMethod httpMethod = new PostMethod(url);

        setQueryString(httpMethod, queryMap);
        setHeaders(httpMethod, headers);

        httpMethod.setRequestBody(body);

        this.httpMethod = httpMethod;
        httpClient.executeMethod(httpMethod);

        response = readRespData(httpMethod);
        return response;
    }

    public int getHttpStatus() {
        if (null == httpMethod) {
            return 0;
        }
        return httpMethod.getStatusCode();
    }

    public String getHttpResponse() {
        return response;
    }

    private void setQueryString(HttpMethod httpMethod, Map<String, String> queryMap) {
        if (null == queryMap) {
            return;
        }

        NameValuePair[]queryData = new NameValuePair[queryMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry: queryMap.entrySet()) {
            NameValuePair value = new NameValuePair();
            value.setName(entry.getKey());
            value.setValue(entry.getValue());
            queryData[i++] = value;
        }
        httpMethod.setQueryString(queryData);
    }

    private void setHeaders(HttpMethod httpMethod, Map<String, String> headers) {
        if (null == headers) {
            return;
        }

        for (Map.Entry<String, String> entry: headers.entrySet()) {
            httpMethod.setRequestHeader(entry.getKey(), entry.getValue());
        }
    }

    private String readRespData(HttpMethod httpMethod) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpMethod.getResponseBodyAsStream(),"utf-8"));
        StringBuffer resp = new StringBuffer();
        String line;
        while (null != (line = reader.readLine())) {
            resp.append(line + "\n");
        }
        reader.close();

        return resp.toString();
    }

}
