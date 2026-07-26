package screens;

import com.badlogic.gdx.Screen;
import online.NetManager;
import online.WsServer;
import online.WsServerRunner;

import java.io.IOException;

public class Provisorio implements Screen, NetManager {
    private WsServer server;

    @Override
    public void show() {
        this.server = new WsServer();
//        WsServer.setNetManager(this);
        WsServerRunner runner = new WsServerRunner();
        runner.start();
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void connect(boolean state) {

    }

    @Override
    public void moveCar(String move, int client) {

    }

    @Override
    public void placeNewPlayer(int connectedUsers, String carBrand) throws IOException {

    }

    @Override
    public void deleteRacer(int index) throws IOException {

    }

    @Override
    public void createBots() throws IOException {

    }
}
