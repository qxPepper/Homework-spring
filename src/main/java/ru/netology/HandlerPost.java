package ru.netology;

import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HandlerPost implements Handler {
    // В ответе POST в качестве body берём body из запроса
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try (responseStream) {
            final var path = request.getPath();
            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);

            final var requestBody = request.getRequestBody();
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            "Message-body: " + requestBody
            ).getBytes());
            responseStream.flush();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
