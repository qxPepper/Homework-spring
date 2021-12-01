package ru.netology;

public class Request {
    private final RequestLine requestLine;
    private final String requestHeader;
    private final String requestBody;

    private final String method;
    private final String path;
    private final String queryString;

    public Request(RequestLine requestLine, String requestHeader, String requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;

        method = requestLine.getMethod();
        path = requestLine.getPath();
        queryString = requestLine.getQueryString();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getRequestHeader() {
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
