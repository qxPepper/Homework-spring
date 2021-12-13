package ru.netology;

import org.apache.http.NameValuePair;

import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class HandlerGet implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try (responseStream) {
            System.out.println("Парсим запрос GET:");

            final var path = request.getPath();
            final var queryString = request.getQueryString();
            System.out.println("path: " + path);
            System.out.println("queryString: " + queryString);

            // getQueryParam(String name), getQueryParams()
            List<NameValuePair> pairs = request.getQueryParams();
            request.defineHost();
            for (NameValuePair pair : pairs) {
                System.out.println("key: " + pair.getName() +
                        ", value: " + request.getQueryParam(pair.getName()));
            }
            System.out.println("**************************************");

            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);

            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.write(content);
                responseStream.flush();
                return;
            }

            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());

            Files.copy(filePath, responseStream);
            responseStream.flush();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
