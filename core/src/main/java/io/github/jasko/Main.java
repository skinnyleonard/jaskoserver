package io.github.jasko;

import com.badlogic.gdx.Game;

import screens.MenuScreen;
import screens.PlayScreen;
import screens.Provisorio;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create() {
//        setScreen(new PlayScreen());
        setScreen(new Provisorio());
    }

    @Override
    public void render() {
    	super.render();
    }

    @Override
    public void dispose() {
    	getScreen().dispose();
    }
}
