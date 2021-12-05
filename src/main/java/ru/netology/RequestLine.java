package ru.netology;

public class RequestLine {
    private final String method;
    private final String path;
    private final String queryString;
    private final String version;
    private final boolean correct;

    public RequestLine(String requestLine) {
        var requestLineArray = requestLine.split(" ");

        if (requestLineArray.length == 3) {
            method = requestLineArray[0];

            int index;
            if ((index = requestLineArray[1].indexOf('?')) == -1) {
                path = requestLineArray[1];
                queryString = "";
            } else {
                path = requestLineArray[1].substring(0, index);
                queryString = requestLineArray[1].substring(index + 1, requestLineArray[1].length());
            }
            version = requestLineArray[2];
            correct = true;
        } else {
            method = "";
            path = "";
            queryString = "";
            version = "";
            correct = false;
        }
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

    public String getVersion() {
        return version;
    }

    public boolean isCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return method + " " + path + "?" + queryString + " " + version;
    }
}
