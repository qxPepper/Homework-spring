package ru.netology;

import org.apache.http.NameValuePair;

import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HandlerPost implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try (responseStream) {
            System.out.println("Парсим запрос POST:");

            final var requestPath = request.getPath();
            final var requestHeader = request.getRequestHeader();
            final var requestBody = request.getRequestBody();
            final var queryString = request.getQueryString();

            if (requestBody.equals("")) {
                System.out.println("запрос POST без тела");
            }
            System.out.println("path: " + requestPath);
            System.out.println("queryString: " + queryString);

            // getQueryParam(String name), getQueryParams()
            List<NameValuePair> pairs = request.getQueryParams();
            for (NameValuePair pair : pairs) {
                System.out.println("key: " + pair.getName() +
                        ", value: " + request.getQueryParam(pair.getName()));
            }
            System.out.println("**************************************");

            final var filePath = Path.of(".", "public", requestPath);
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);

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
