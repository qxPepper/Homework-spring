package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPool {
    List<String> validPaths = List.of(
            "/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css",
            "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public void listen(int serverPort) {
        final ExecutorService threadPool = Executors.newFixedThreadPool(64);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                Server server = new Server(clientSocket, this);
                threadPool.execute(server);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
