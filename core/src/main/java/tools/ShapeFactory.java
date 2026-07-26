package tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class ShapeFactory {
    private ShapeFactory() {}

    public static Body createRectangle(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density, boolean sensor) {
        final BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / Constants.PPM, position.y / Constants.PPM);
        bdef.type = type;
        final Body body = world.createBody(bdef);

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / Constants.PPM, size.y / Constants.PPM);
        final FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.isSensor = sensor;

        body.createFixture(fdef);
        shape.dispose();

        return body;
    }

    public static Body createPlayer(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density, boolean sensor, String carUser) {
        final BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / Constants.PPM, position.y / Constants.PPM);
        bdef.type = type;
        final Body body = world.createBody(bdef);

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / Constants.PPM, size.y / Constants.PPM);
        final FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.isSensor = sensor;

        body.createFixture(fdef).setUserData(carUser);
        shape.dispose();

        return body;
    }


    public static Body createPolyline(Vector2[] worldVertices, final BodyDef.BodyType type, final World world, float density, boolean sensor, String name) {
        final BodyDef bdef = new BodyDef();
        final Body body = world.createBody(bdef);

        final ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);

        bdef.type = type;

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = chain;
        fdef.density = density;
        fdef.isSensor = sensor;

        body.createFixture(fdef).setUserData(name);
        chain.dispose();

        return body;
    }

    public static Body createPolygon(float[] vertices, final BodyDef.BodyType type, final World world, float density, boolean sensor) {
        final BodyDef bdef = new BodyDef();
        final Body body = world.createBody(bdef);

        final PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        bdef.type = type;

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.isSensor = sensor;

        body.createFixture(fdef);
        shape.dispose();

        return body;
    }

    public static Body createCheck(Vector2[] worldVertices, final BodyDef.BodyType type, final World world, float density, boolean sensor, String name) {
        final BodyDef bdef = new BodyDef();
        final Body body = world.createBody(bdef);

        final ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);

        bdef.type = type;

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = chain;
        fdef.density = density;
        fdef.isSensor = sensor;

        body.createFixture(fdef).setUserData(name);
        chain.dispose();

        return body;
    }
}
