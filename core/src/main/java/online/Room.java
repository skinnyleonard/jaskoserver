package online;

import org.xml.sax.SAXException;
import screens.GameScreen;
import screens.PlayScreen;

import javax.websocket.Session;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Room {
    private String id;
    public String roomName;
    private Thread thread;
    private boolean running = true;
    private GameScreen gameScreen;
    public ArrayList<User> users = new ArrayList<User>();
    public int clientIndexed = 0;
    public NetManager netManager;
    public int connectedUsers = 0;
    public String move = "";
    public boolean[] gridPositions = new boolean[8];
    public HashMap<String, Integer> userPositions = new HashMap<>();

    public Room(String id, String category, String roomName) throws ParserConfigurationException, IOException, SAXException {
        this.id = id;
        this.gameScreen = new GameScreen(category, this);
        this.roomName = roomName;
        startPlayScreen();
    }

    public int assignPosition(String userId) {
        for(int i=0; i<gridPositions.length; i++){
            if(!gridPositions[i]){
                gridPositions[i] = true;
                userPositions.put(userId, i);
//                System.out.println("LA POSICION ASIGNADA ES: "+i);
                return i;
            }
        }
        return -1;
    }

    public void leftPosition(String userId) {
        Integer position = userPositions.remove(userId);
        if(position != null) {
            gridPositions[position] = false;
        }
    }

    private void startPlayScreen() {
        thread = new Thread(() -> {
            long last = System.nanoTime();
            while(running) {
                long now = System.nanoTime();
                float delta = (now - last) / 1_000_000_000f;
                last = now;
                try {
                    gameScreen.update(delta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {}
            }
        });
        thread.start();
    }

    public User getUser(String id) {
        int i = 0;
        int indexUser = -1;
        while (i < users.size() && indexUser == -1) {
            User user = users.get(i);
            if (id.equals(user.getId())) {
                indexUser = i;
            }
            i++;
        }
        return users.get(indexUser);
    }

    public int getIndexUser(String id) {
        int i = 0;
        int indexUser = -1;
        while (i < users.size() && indexUser == -1) {
            User user = users.get(i);
            if (id.equals(user.getId())) {
                indexUser = i;
            }
            i++;
        }
        return indexUser;
    }

    public void pingEveryone(String message) throws IOException {
        for (User user : users) {
            user.getSession().getBasicRemote().sendText(message);
        }
    }

    public String getId() { return id; }

    public void setNetManager(GameScreen gameScreen) {
        this.netManager = gameScreen;
    }
}
