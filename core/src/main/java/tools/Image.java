package tools;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Image {
    private Texture t;
    private Sprite s;
    public String route;

    public Image(String route){
        t = new Texture(route);
        s = new Sprite(t);
        this.route = route;
    }

    public void draw(){
        s.draw(Render.batch);
    }

    public void setSize (float width, float height){
        s.setSize(width, height);
    }

    public void setPosition(float x, float y){
        s.setPosition(x, y);
    }

    public float getWidth(){
        return s.getWidth();
    }

    public float getHeight(){
        return s.getHeight();
    }
}
