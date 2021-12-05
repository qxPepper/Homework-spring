package ru.netology;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Server implements Runnable {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final int BUFFER_LIMIT = 4096;

    private final Socket clientSocket;
    private final ServerPool serverPool;
    private final List<String> allowedMethods = List.of(GET, POST);

    private byte[] buffer;
    private int read;
    private int requestLineEnd;
    private byte[] requestLineDelimiter;
    private byte[] headersDelimiter;

    private RequestLine requestLine;
    private List<String> requestHeader;
    private String requestBody;

    public Server(Socket clientSocket, ServerPool serverPool) {
        this.clientSocket = clientSocket;
        this.serverPool = serverPool;
    }

    @Override
    public void run() {
        try (final var input = new BufferedInputStream(clientSocket.getInputStream());
             final var output = new BufferedOutputStream(clientSocket.getOutputStream())) {
            final var limit = BUFFER_LIMIT;

            input.mark(limit);
            buffer = new byte[limit];
            read = input.read(buffer);

            if (!defineRequestLine() || !defineRequestHeader(input)) {
                badRequest(output);
                return;
            }

            requestBody = defineRequestBody(input);

            Request request = new Request(requestLine, requestHeader, requestBody);
            showRequest(request);

            var handler = handlerSearch(requestLine.getMethod(), requestLine.getPath());
            if (handler == null) {
                badRequest(output);
                return;
            }
            handler.handle(request, output);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean defineRequestLine() {
        requestLineDelimiter = new byte[]{'\r', '\n'};
        requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);

        if (requestLineEnd == -1) {
            return false;
        }

        requestLine = new RequestLine(new String(Arrays.copyOf(buffer, requestLineEnd)));

        return requestLine.isCorrect() &&
                allowedMethods.contains(requestLine.getMethod()) &&
                requestLine.getPath().startsWith("/");
    }

    public boolean defineRequestHeader(BufferedInputStream input) throws IOException {
        headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);

        if (headersEnd == -1) {
            return false;
        }

        input.reset();
        input.skip(headersStart);

        final var headersBytes = input.readNBytes(headersEnd - headersStart);
        requestHeader = Arrays.asList(new String(headersBytes).split("\r\n"));

        return true;
    }

    public String defineRequestBody(BufferedInputStream input) throws IOException {
        requestBody = "";
        if (!requestLine.getMethod().equals(GET)) {
            input.skip(headersDelimiter.length);
            final var contentLength = extractHeader(requestHeader);
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = input.readNBytes(length);

                requestBody = new String(bodyBytes);
            }
        }
        return requestBody;
    }

    public Handler handlerSearch(String requestMethod, String requestPath) {
        if (requestMethod.equals("GET")) {
            return serverPool.mapGet.get(requestPath);
        } else {
            return serverPool.mapPost.get(requestPath);
        }
    }

    private static Optional<String> extractHeader(List<String> headers) {
        return headers.stream()
                .filter(o -> o.startsWith("Content-Length"))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    public void showRequest(Request request) {
        System.out.println();
        System.out.println("Поступил запрос:");
        System.out.println("requestLine:");
        System.out.println(request.getRequestLine());
        System.out.println();

        System.out.println("requestHeader:");
        System.out.println(request.getRequestHeader());
        System.out.println();

        System.out.println("requestBody:");
        System.out.println(request.getRequestBody());
        System.out.println("......................................");
        System.out.println();
    }
}
