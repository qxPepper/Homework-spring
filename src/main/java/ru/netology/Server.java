package ru.netology;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

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
                var requestLine = input.readLine();
                if (requestLine == null) {
                    continue;
                }
                var parts = requestLine.split(" ");

                if (parts.length != 3) {
                    continue;
                }

                var path = parts[1];
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

                var filePath = Path.of(".", "public", path);
                var mimeType = Files.probeContentType(filePath);

                if (path.equals("/classic.html")) {
                    var template = Files.readString(filePath);
                    var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    output.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    output.write(content);
                    output.flush();
                    continue;
                }

                var length = Files.size(filePath);
                output.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, output);
                output.flush();
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
