package ru.netology;

public class Main {
    public static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        final var serverPool = new ServerPool();

        serverPool.listen(SERVER_PORT);
    }
}
