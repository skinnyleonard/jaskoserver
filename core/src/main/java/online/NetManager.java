package online;

import java.io.IOException;

public interface NetManager {
    void connect(boolean state);
    void moveCar(String move, int client);
    public static String move = "";
    void placeNewPlayer(int connectedUsers, String carBrand) throws IOException;
    void deleteRacer(int index) throws IOException;
    void createBots() throws IOException;
}
