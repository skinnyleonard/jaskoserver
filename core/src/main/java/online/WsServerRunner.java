package online;

import org.glassfish.tyrus.server.Server;

public class WsServerRunner extends Thread {
    @Override
    public void run() {
        try {
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            Server server = new Server("0.0.0.0", port, "/ws", null, WsServer.class);
            server.start();
            System.out.println("prendio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
