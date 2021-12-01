package ru.netology;

import java.io.*;
import java.net.Socket;

public class Server implements Runnable {
    private final Socket clientSocket;
    private final ServerPool serverPool;

    public Server(Socket clientSocket, ServerPool serverPool) {
        this.clientSocket = clientSocket;
        this.serverPool = serverPool;
    }

    @Override
    public void run() {
        try (final var input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             final var output = new BufferedOutputStream(clientSocket.getOutputStream())) {
            while (true) {
                var readInfo = input.readLine();
                var requestLine = new RequestLine(readInfo);

                if (!requestLine.isCorrect()) {
                    continue;
                }

                var path = requestLine.getPath();
                if (!serverPool.validPaths.contains(path)) {
                    output.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    output.flush();
                    continue;
                }

                var message = new StringBuilder();
                String value;
                while (true) {
                    value = input.readLine();
                    if (value.equals("")) {
                        break;
                    }
                    message.append(value);
                }
                var arrMessage = message.toString().split("\r\n\r\n");
                var requestHeader = arrMessage[0];

                var requestBody = "";
                if (arrMessage.length == 2) {
                    requestBody = arrMessage[1];
                }

                var request = new Request(requestLine, requestHeader, requestBody);

                var method = requestLine.getMethod();
                var handler = handlerSearch(method, path);
                handler.handle(request, output);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public Handler handlerSearch(String requestMethod, String requestPath) {
        if (requestMethod.equals("GET")) {
            return serverPool.mapGet.get(requestPath);
        } else {
            return serverPool.mapPost.get(requestPath);
        }
    }
}
