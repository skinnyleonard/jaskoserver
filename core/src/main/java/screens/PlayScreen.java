package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entities.Car;
import entities.Wheel;
import online.*;
import org.xml.sax.SAXException;
import tools.*;

import javax.xml.parsers.ParserConfigurationException;

import static entities.Car.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class PlayScreen implements Screen, NetManager {

    public static SpriteBatch batch;
    private final World world;
    private final Box2DDebugRenderer b2dr;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    public final Loader mapLoader;
    private HUD hud;
    private ArrayList<Car> players = new ArrayList<Car>();
    private WorldContactListener wcl;
    private float drift;
    public WsServer server;

    public PlayScreen(String category) throws ParserConfigurationException, IOException, SAXException {
        batch = new SpriteBatch();
        world = new World(Constants.GRAVITY, true);
        b2dr = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.zoom = Constants.DEFUALT_ZOOM;
        hud = new HUD(batch);
        viewport = new ExtendViewport(640 / Constants.PPM, 480 / Constants.PPM, camera);
//        mapLoader = new MapLoader(world);
        mapLoader = new Loader(world);
//        wcl = new WorldContactListener(this);
        world.setContactListener(wcl);
        drift = (category == "F1") ? 0 : 0.8f;
    }

    @Override
    public void show() {
        this.server = new WsServer();
//        WsServer.setNetManager(this);
        WsServerRunner runner = new WsServerRunner();
        runner.start();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(!players.isEmpty()){
//            moveCar(server.move, server.clientIndexed);
        }
        try {
            update(delta);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        draw();
        hud.stage.draw();
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

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        b2dr.render(world, camera.combined);
    }

    private void update(final float delta) throws IOException {
        for(Car car : players) {
            car.update(delta);
        }
        if(this.players.size() < 1){
//            this.server.pingEveryone("updateGrid;"+wcl.posCheck(players.size()));
        }
//aca empieza
//        try{
//            for(int i = 0; i<Math.min(server.users.size(), players.size()); i++) {
////                User user = server.users.get(i);
//                Car car = players.get(i);
//
////                ByteArrayOutputStream baos = new ByteArrayOutputStream();
////                DataOutputStream dos = new DataOutputStream(baos);
//
//                float x = Float.parseFloat(car.getMetrics().split("%")[0]);
//                float y = Float.parseFloat(car.getMetrics().split("%")[1]);
//                float angle = Float.parseFloat(car.getMetrics().split("%")[2]);
//
////                dos.writeUTF("updatePos");
////                dos.writeFloat(x);
////                dos.writeFloat(y);
////                dos.writeFloat(angle);
////                dos.flush();
////
////                byte[] data = baos.toByteArray();
//
////                DatagramPacket packet = new DatagramPacket(
////                    data, data.length, user.getIp(), user.getPort()
////                );
////                server.socket.send(packet);
//                server.sendMessage("updatePos;"+x+";"+y+";"+angle, user.getSession());
//
//                String personalizedJson = updateOthersPos(car);
//                server.sendMessage("updateOthersPos;"+personalizedJson, user.getSession());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//aca termina

        camera.position.set(new Vector2(12800/2f, 6912/2f), 0);
        camera.setToOrtho(false, 640/Constants.PPM*3, 480/Constants.PPM*3);
        camera.update();
        world.step(delta, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        try {
            this.server.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        batch.dispose();
        world.dispose();
        b2dr.dispose();
//        mapLoader.dispose();
    }

    @Override
    public void connect(boolean state) {

    }

    @Override
    public void moveCar(String move, int client) {
//        if(server.clientIndexed != -1) {
//            if(move.equals("up")) {
//                players.get(client).setDriveDirection(DRIVE_DIRECTION_FORWARD);
//                players.get(client).imageIterationNumber = 1;
//                players.get(client).flip = false;
//            } else if(move.equals("down")) {
//                players.get(client).setDriveDirection(DRIVE_DIRECTION_BACKWARD);
//                players.get(client).imageIterationNumber = 1;
//                players.get(client).flip = false;
//            } else if(move.equals("afk")) {
//                players.get(client).setDriveDirection(DRIVE_DIRECTION_NONE);
//                players.get(client).imageIterationNumber = 1;
//                players.get(client).flip = false;
//            }
//
//            if(move.equals("left")) {
//                players.get(client).setTurnDirection(TURN_DIRECTION_LEFT);
//                players.get(client).imageIterationNumber = 3;
//                players.get(client).flip = false;
//            } else if(move.equals("right")) {
//                players.get(client).setTurnDirection(TURN_DIRECTION_RIGHT);
//                players.get(client).imageIterationNumber = 3;
//                players.get(client).flip = true;
//            } else if(move.equals("afk")) {
//                players.get(client).setTurnDirection(TURN_DIRECTION_NONE);
//                players.get(client).imageIterationNumber = 1;
//                players.get(client).flip = false;
//            }
//        }
    }

    @Override
    public void placeNewPlayer(int connectedUsers, String carBrand) throws IOException {
        int arrPosition = mapLoader.positions.size() - (connectedUsers);
        float x = mapLoader.positions.get(arrPosition).x;
        float y = mapLoader.positions.get(arrPosition).y;
        players.add(new Car(35.0f, drift, 60, mapLoader, DRIVE_4WD, world, x, y, connectedUsers-1, carBrand));
//        this.server.pingEveryone("newCar;"+generatePositionsJSON());
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
//        this.server.pingEveryone("contricantdisconnected;"+players.get(index).id);
        this.players.remove(index);

//        server.clientIndexed = -1;

        wcl.removeCar(index);
    }

    @Override
    public void createBots() throws IOException {
        for(int i = 2; i<6; i++) {
            placeNewPlayer(i, "subaru");
        }
    }
}
