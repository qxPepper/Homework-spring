package ru.netology;

import org.apache.http.NameValuePair;

import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerPost implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try (responseStream) {
            System.out.println("Парсим запрос POST:");

            final var requestPath = request.getPath();
            final var queryString = request.getQueryString();
            final var requestHeader = request.getRequestHeader();
            final var requestBody = request.getRequestBody();
            System.out.println("path: " + requestPath);
            System.out.println("queryString: " + queryString);

            // getQueryParam(String name), getQueryParams()
            List<NameValuePair> pairs = request.getQueryParams();
            for (NameValuePair pair : pairs) {
                System.out.println("key: " + pair.getName() +
                        ", value: " + request.getQueryParam(pair.getName()));
            }
            System.out.println("......................................");

            if (!requestBody.equals("")) {
                var map = new ConcurrentHashMap<String, String>();

                if (requestHeader.contains("Content-Type: application/x-www-form-urlencoded")) {
                    // getPostParam(String name), getPostParams()
                    System.out.println("x-www-form-urlencoded:");

                    map = request.getPostParams();
                    for (String elm : map.keySet()) {
                        System.out.println("key: " + elm + ", value: " + request.getPostParam(elm));
                    }

                } else {
                    // getPart(String name), getParts()
                    System.out.println("multipart/form-data:");

                    map = request.getParts();
                    for (String elm : map.keySet()) {
                        System.out.println("key: " + elm + ", value: " + request.getPart(elm));
                    }
                }
            } else {
                System.out.println("запрос POST с пустым телом");
            }
            System.out.println("**************************************");

            final var filePath = Path.of(".", "public", requestPath);
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);

            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + "application/x-www-form-urlencoded" + "\r\n" +
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
