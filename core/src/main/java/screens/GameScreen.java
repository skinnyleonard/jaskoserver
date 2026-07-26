package screens;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonWriter;
import entities.Car;
import entities.Wheel;
import online.*;
import org.xml.sax.SAXException;
import tools.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import static entities.Car.*;
import static entities.Car.DRIVE_4WD;
import static entities.Car.TURN_DIRECTION_LEFT;
import static entities.Car.TURN_DIRECTION_NONE;
import static entities.Car.TURN_DIRECTION_RIGHT;

public class GameScreen implements NetManager {
    private ArrayList<Car> players = new ArrayList<Car>();
    private WorldContactListener wcl;
    private float drift;
    public WsServer server;
    private final World world;
    public final Loader mapLoader;
    public Room parentRoom;

    public GameScreen(String category, Room room) throws ParserConfigurationException, IOException, SAXException {
        world = new World(Constants.GRAVITY, true);
        mapLoader = new Loader(world);
        drift = (category == "F1") ? 0 : 0.8f;
        wcl = new WorldContactListener(this); //hay que cambiar unos parametros
        world.setContactListener(wcl);
        server = new WsServer();
        this.parentRoom = room;
        parentRoom.setNetManager(this);
    }

    //esto lo voy a tener que invocar despues
    public void update(float delta) throws IOException {
//        System.out.println("el gamescreen esta vivo");
        if(!players.isEmpty()){
            moveCar(parentRoom.move, parentRoom.clientIndexed);
        }

        for(Car car : players) {
            car.update(delta);
        }
        if(this.players.size() > 0){
            this.parentRoom.pingEveryone("updateGrid;"+wcl.posCheck(players.size()));
        }
        //aca empieza
        try{
            for(int i = 0; i<Math.min(parentRoom.users.size(), players.size()); i++) {
                User user = parentRoom.users.get(i);
                Car car = players.get(i);

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                DataOutputStream dos = new DataOutputStream(baos);

                float x = Float.parseFloat(car.getMetrics().split("%")[0]);
                float y = Float.parseFloat(car.getMetrics().split("%")[1]);
                float angle = Float.parseFloat(car.getMetrics().split("%")[2]);

//                dos.writeUTF("updatePos");
//                dos.writeFloat(x);
//                dos.writeFloat(y);
//                dos.writeFloat(angle);
//                dos.flush();
//
//                byte[] data = baos.toByteArray();

//                DatagramPacket packet = new DatagramPacket(
//                    data, data.length, user.getIp(), user.getPort()
//                );
//                server.socket.send(packet);
                server.sendMessage("updatePos;"+x+";"+y+";"+angle, user.getSession());

                String personalizedJson = updateOthersPos(car);
                server.sendMessage("updateOthersPos;"+personalizedJson, user.getSession());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //aca termina
        world.step(delta, 6, 2);
    }

    private String generatePositionsJSON() {
        StringWriter sw = new StringWriter();
        JsonWriter writter = new JsonWriter(sw);
        writter.setOutputType(JsonWriter.OutputType.json);

        try{
            writter.object();
            writter.name("players").array();

            int i=0;
            for(Car car : players) {
                writter.object();
                writter.name("id").value(car.id);
                writter.name("x").value(Float.parseFloat(car.getMetrics().split("%")[0]));
                writter.name("y").value(Float.parseFloat(car.getMetrics().split("%")[1]));
                writter.name("path").value("cars/"+car.carBrand+"/"+car.imageIterationNumber+".png");
                writter.name("flip").value(car.flip);
                writter.pop();
                i++;
            }
            writter.pop();
            writter.pop();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    private String updateOthersPos(Car ref) {
        StringWriter sw = new StringWriter();
        JsonWriter writter = new JsonWriter(sw);
        writter.setOutputType(JsonWriter.OutputType.json);

        try{
            writter.object();
            writter.name("players").array();

            for(int j=0;j<players.size();j++) {

                Car otherCar = players.get(j);
                Relationship.RelationshipType rel = Relationship.classifyRelationship(otherCar.getBody(), ref.getBody());

                writter.object();
                writter.name("id").value(otherCar.id);
                writter.name("x").value(Float.parseFloat(otherCar.getMetrics().split("%")[0]));
                writter.name("y").value(Float.parseFloat(otherCar.getMetrics().split("%")[1]));

                if (rel.equals(Relationship.RelationshipType.PERPENDICULAR_LEFT)) {
                    writter.name("path").value("cars/" + otherCar.carBrand + "/4.png");
                    writter.name("flip").value(otherCar.flip);
                } else if (rel.equals(Relationship.RelationshipType.PERPENDICULAR_RIGHT)) {
                    writter.name("path").value("cars/" + otherCar.carBrand + "/4.png");
                    writter.name("flip").value(true);
                } else if (rel.equals(Relationship.RelationshipType.PERPENDICULAR)) {
                    writter.name("path").value("cars/" + otherCar.carBrand + "/4.png");
                    writter.name("flip").value(otherCar.flip);
                } else if (rel.equals(Relationship.RelationshipType.CONFRONTED)) {
                    writter.name("path").value("cars/" + otherCar.carBrand + "/6.png");
                    writter.name("flip").value(otherCar.flip);
                } else if (rel.equals(Relationship.RelationshipType.NONE)) {
                    writter.name("path").value("cars/" + otherCar.carBrand + "/" + otherCar.imageIterationNumber + ".png");
                    writter.name("flip").value(otherCar.flip);
                }
                writter.pop();
            }
            writter.pop();
            writter.pop();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    @Override
    public void connect(boolean state) {

    }

    @Override
    public void moveCar(String move, int client) {
        if(parentRoom.clientIndexed != -1) {
            if(move.equals("up")) {
                players.get(client).setDriveDirection(DRIVE_DIRECTION_FORWARD);
                players.get(client).imageIterationNumber = 1;
                players.get(client).flip = false;
            } else if(move.equals("down")) {
                players.get(client).setDriveDirection(DRIVE_DIRECTION_BACKWARD);
                players.get(client).imageIterationNumber = 1;
                players.get(client).flip = false;
            } else if(move.equals("afk")) {
                players.get(client).setDriveDirection(DRIVE_DIRECTION_NONE);
                players.get(client).imageIterationNumber = 1;
                players.get(client).flip = false;
            }

            if(move.equals("left")) {
                players.get(client).setTurnDirection(TURN_DIRECTION_LEFT);
                players.get(client).imageIterationNumber = 3;
                players.get(client).flip = false;
            } else if(move.equals("right")) {
                players.get(client).setTurnDirection(TURN_DIRECTION_RIGHT);
                players.get(client).imageIterationNumber = 3;
                players.get(client).flip = true;
            } else if(move.equals("afk")) {
                players.get(client).setTurnDirection(TURN_DIRECTION_NONE);
                players.get(client).imageIterationNumber = 1;
                players.get(client).flip = false;
            }
        }
    }

    @Override
    public void placeNewPlayer(int connectedUsers, String carBrand) throws IOException {
        int arrPosition = mapLoader.positions.size() - (connectedUsers);
        float x = mapLoader.positions.get(arrPosition).x;
        float y = mapLoader.positions.get(arrPosition).y;
        players.add(new Car(35.0f, drift, 60, mapLoader, DRIVE_4WD, world, x, y, connectedUsers-1, carBrand));
        this.parentRoom.pingEveryone("newCar;"+generatePositionsJSON());
    }

    @Override
    public void deleteRacer(int index) throws IOException {
        Body playerToDestroy = this.players.get(index).getBody();
        Array<JointEdge> jointEdges = new Array<>(playerToDestroy.getJointList());
        for(JointEdge edge : jointEdges) {
            Joint joint = edge.joint;
            world.destroyJoint(joint);
        }

        for(Wheel wheel : this.players.get(index).mAllWheels) {
            world.destroyBody(wheel.getBody());
        }
        world.destroyBody(playerToDestroy);
        this.parentRoom.pingEveryone("contricantdisconnected;"+players.get(index).id);
        this.players.remove(index);

        parentRoom.clientIndexed = -1;

        wcl.removeCar(index);
    }

    @Override
    public void createBots() throws IOException {
        for(int i = 2; i<6; i++) {
            placeNewPlayer(i, "subaru");
        }
    }
}
