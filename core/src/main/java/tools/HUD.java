package tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD implements Disposable{
    public Stage stage;
    private Viewport viewport;

    private Integer score;

    Label scoreLabel;
    public static Label debugLabel;
    public static Label checkLabel;
    public static Label timeLabel;
    public static Label lapLabel;
    public static Label leaderboard;


    public HUD(SpriteBatch sb) {
        score = 0;

        viewport = new ExtendViewport(640, 480, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);


        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("Tiempo: ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        debugLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        checkLabel = new Label("Cargando..." ,new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lapLabel = new Label("Cargando..." ,new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        leaderboard = new Label("Cargando...", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Timer contador = new Timer();
        contador.start();


        table.add(leaderboard).expandX().padTop(10);
        table.add(checkLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(lapLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(debugLabel).expandX();

        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
