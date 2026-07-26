package online;

import com.badlogic.gdx.utils.JsonWriter;
import org.glassfish.tyrus.server.Server;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.teavm.jso.websocket.WebSocket;
import org.xml.sax.SAXException;

@ServerEndpoint("/server")
public class WsServer {
    public static HashMap<String, Room> rooms = new HashMap<>();
    private final int MAX_CLIENTS = 8;
    private final int MAX_ROOMS = 10;
    public static Server server;

    @OnOpen
    public void onOpen(Session session) throws IOException, ParserConfigurationException, SAXException {

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        try{
            String roomIndex = getRoomByUser(session).getId();
            int userIndex = rooms.get(roomIndex).getIndexUser(session.getId());
            rooms.get(roomIndex).users.remove(userIndex);
            rooms.get(roomIndex).netManager.deleteRacer(userIndex);
            rooms.get(roomIndex).leftPosition(session.getId());
            rooms.get(roomIndex).connectedUsers--;
        } catch (NullPointerException e){
            System.out.println("se uso create server");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, ParserConfigurationException, SAXException {
        System.out.println(message);

        String[] parts = message.split("\\$");

        switch (parts[0]) {
            case "createServer":
                if(rooms.size() >= MAX_ROOMS) {
                    session.getBasicRemote().sendText("maxrooms");
                } else {
                    String uuid = UUID.randomUUID().toString().replace("-", "");
                    Room room = new Room(uuid, parts[1], parts[2]);
                    rooms.put(uuid, room);
                    session.getBasicRemote().sendText("uuid;"+uuid);
                }
                break;
            case "getRooms":
                session.getBasicRemote().sendText("rooms;"+generateRoomsJSON());
                break;
            case "connect":
                if(rooms.get(parts[1]).users.size() >= MAX_CLIENTS) {
                    session.getBasicRemote().sendText("serverfull");
                } else{
                    int id = rooms.get(parts[1]).assignPosition(session.getId());
                    System.err.println("EL ID QUE LE VOY A PASAR: "+id);
                    session.getBasicRemote().sendText("connected;" + id);
                    rooms.get(parts[1]).connectedUsers++;
                    User newUser = new User(session.getId(), parts[3], session); //<- es el username
                    rooms.get(parts[1]).users.add(newUser);
                    rooms.get(parts[1]).netManager.placeNewPlayer((id+1), parts[2]); //<- era la marca del auto
                }
                break;
            case "move":
                System.out.println("el usuario se mueve");
                rooms.get(parts[1]).move = parts[2];
                rooms.get(parts[1]).clientIndexed = rooms.get(parts[1]).getIndexUser(session.getId());
                rooms.get(parts[1]).netManager.moveCar(parts[2], rooms.get(parts[1]).getIndexUser(session.getId()));
                break;
        }
    }

    private String generateRoomsJSON() {
        StringWriter sw = new StringWriter();
        JsonWriter jw = new JsonWriter(sw);
        jw.setOutputType(JsonWriter.OutputType.json);

        try{
            jw.object();
            jw.name("rooms").array();

            for(Room room : rooms.values()){
                jw.object();
                jw.name("id").value(room.getId());
                jw.name("roomName").value(room.roomName);
                jw.name("connectedUsers").value(room.connectedUsers);
                jw.pop();
            }
            jw.pop();
            jw.pop();
        } catch(Exception e){
            e.printStackTrace();
            return "no hay rooms";
        }
        return sw.toString();
    }

    public void sendMessage(String message, Session user) throws IOException {
        user.getBasicRemote().sendText(message);
    }

    private Room getRoomByUser(Session user) {
        for(Room room : rooms.values()){
            for(User usr : room.users) {
                if(usr.getSession().equals(user)){
                    return room;
                }
            }
        }
        return null;
    }

    public void finish() throws IOException {
        System.out.println("me voy a la mierda");
        server.stop();
    }
}
