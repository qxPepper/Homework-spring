package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Request {
    public static final String HOST = "http://localhost:9999";
    private final RequestLine requestLine;
    private final List<String> requestHeader;
    private final String requestBody;

    private final String method;
    private final String path;
    private final String queryString;

    public Request(RequestLine requestLine, List<String> requestHeader, String requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;

        method = requestLine.getMethod();
        path = requestLine.getPath();
        queryString = requestLine.getQueryString();
    }

    public List<NameValuePair> getQueryParams() throws URISyntaxException {
        var uri = new URI(HOST + path + "?" + queryString);
        return URLEncodedUtils.parse(uri, String.valueOf(StandardCharsets.UTF_8));
    }

    public String getQueryParam(String name) {
        String value = "";
        try {
            var pairs = getQueryParams();
            for (NameValuePair pair : pairs) {
                if (pair.getName().equals(name)) {
                    value = pair.getValue();
                    break;
                }
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return value;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public List<String> getRequestHeader() {
        return requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
