package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import io.github.jasko.Main;
import org.xml.sax.SAXException;
import tools.Constants;
import tools.Image;
import tools.Text;
import tools.Render;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MenuScreen implements Screen {

    private Main game;
    private SpriteBatch b;

    private Image[] maps;
    private int mapIndex = 0;

    private String[] categories = {"F1", "Rally", "Drift"};
    private int catIndex = 0;
    private Text categoryText;
    public float time = 0;

    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        b = Render.batch;

        maps = new Image[] {
            new Image("mapas/mapa.png"),
        };

        categoryText = new Text(Constants.MENUFONT, 50, Color.WHITE);
    }

    @Override
    public void render(float delta) {
        time += delta;

        Render.cleanScreen(0, 0, 0);
        b.begin();

        Image currentMap = maps[mapIndex];

        currentMap.setSize(
            Gdx.graphics.getWidth() * 0.35f,
            Gdx.graphics.getHeight() * 0.45f
        );

        currentMap.setPosition(
            Gdx.graphics.getWidth() * 0.10f,
            (Gdx.graphics.getHeight() - currentMap.getHeight()) / 2
        );

        currentMap.draw();


        categoryText.setTexto("" + categories[catIndex]);
        categoryText.setPosition(
            (int)(Gdx.graphics.getWidth() * 0.60f),
            Gdx.graphics.getHeight() / 2
        );
        categoryText.dibujar();

        b.end();


        if (time > 0.20f) {

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                mapIndex = (mapIndex - 1 + maps.length) % maps.length;
                time = 0;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                mapIndex = (mapIndex + 1) % maps.length;
                time = 0;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                catIndex = (catIndex - 1 + categories.length) % categories.length;
                time = 0;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                catIndex = (catIndex + 1) % categories.length;
                time = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            //                game.setScreen(new PlayScreen(categories[catIndex]));
//                game.setScreen(new GameScreen(categories[catIndex]));
            game.setScreen(new Provisorio());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
