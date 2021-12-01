package ru.netology;

public class Main {
    public static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        final var serverPool = new ServerPool();

        for (String element : serverPool.validPaths) {
            HandlerGet handlerGet = new HandlerGet();
            serverPool.addHandler("GET", element, handlerGet);

            HandlerPost handlerPost = new HandlerPost();
            serverPool.addHandler("POST", element, handlerPost);
        }
        serverPool.listen(SERVER_PORT);
    }
}
