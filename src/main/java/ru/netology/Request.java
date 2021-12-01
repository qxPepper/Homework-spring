package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    public ConcurrentHashMap<String, String> getPostParams() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        final var queries = requestBody.split("&");

        for (String element : queries) {
            var sign = element.indexOf("=");
            var key = element.substring(0, sign);
            var value = element.substring(sign + 1, element.length());
            map.put(key, value);
            // Если будут два и более одинаковых имени (ключа в мапе), то
            // пара ключ-значение будет перезаписываться, в итого получим последнюю пару.
        }
        return map;
    }

    public String getPostParam(String name) {
        var map = getPostParams();
        return map.get(name);
    }

    public ConcurrentHashMap<String, String> getParts() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        var boundary = "";
        for (String header : requestHeader) {
            if (header.contains("boundary")) {
                boundary = header.substring(header.indexOf("=") + 1);
                break;
            }
        }

        for (String paramString : requestBody.split(boundary)) {
            if (paramString.contains("name=")) {
                var startKey = paramString.indexOf("name=");
                var endKey = paramString.indexOf("\r\n\r\n");
                var key = paramString.substring(startKey + "name=".length() + 1, endKey - 1);

                var startValue = endKey + "\r\n\r\n".length();
                var str = paramString.substring(startValue);
                var endStr = str.indexOf("--");
                var value = str.substring(0, endStr - 1);

                map.put(key, value);
            }
        }
        return map;
    }

    public String getPart(String name) {
        var map = getParts();
        return map.get(name);
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
