package online;

import org.teavm.jso.websocket.WebSocket;

public class Fuckingshit {
    static WebSocket webSocket = WebSocket.create("ws://localhost:8080/ws/server");

    public static void connect() {
        webSocket.onOpen(ws -> {
            System.out.println(ws);
        });
    }

    public static void main(String[] args) {
        connect();
    }
}
