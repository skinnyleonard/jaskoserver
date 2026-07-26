package online;

import org.glassfish.tyrus.server.Server;

public class WsServerRunner extends Thread {
    private int port = Integer.parseInt(System.getEnv().getOrDefault("PORT", "8080"))
    @Override
    public void run() {
        try {
            Server server = new Server("localhost", port, "/ws", null, WsServer.class);
            server.start();
            System.out.println("prendio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
