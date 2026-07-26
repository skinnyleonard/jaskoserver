package tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import screens.MenuScreen;

public class InputManager implements InputProcessor {
    private boolean abajo = false, arriba = false;
    private boolean enter = false;
    MenuScreen app;

    public InputManager(MenuScreen app){
        this.app = app;
    }

    public boolean isAbajo(){ return abajo; }
    public boolean isArriba(){ return arriba; }
    public boolean isEnter(){ return enter; }

    @Override
    public boolean keyDown(int keycode) {
        app.time = 0.05f;
        if (keycode == Input.Keys.DOWN) abajo = true;
        if (keycode == Input.Keys.UP)   arriba = true;
        if (keycode == Input.Keys.ENTER) enter = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.DOWN) abajo = false;
        if (keycode == Input.Keys.UP)   arriba = false;
        if (keycode == Input.Keys.ENTER) enter = false;
        return false;
    }

    @Override public boolean keyTyped(char character) { return false; }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
