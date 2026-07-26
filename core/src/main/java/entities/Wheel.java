package entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Wheel extends BodyHolder {
    public static final int UPPER_LEFT = 0;
    public static final int UPPER_RIGHT = 1;
    public static final int DOWN_LEFT = 2;
    public static final int DOWN_RIGHT = 3;

    private final boolean mPowered;
    private Car mCar;

    public Wheel(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, final float density, final int id, Car car, final boolean powered) {
        super(position, size, type, world, density, true, id);
        this.mCar = car;
        this.mPowered = powered;
    }

    public void setAngle(final float angle) {
        getBody().setTransform(getBody().getPosition(), mCar.getBody().getAngle() + angle * MathUtils.degreesToRadians);
    }

    public boolean isPowered() {
        return mPowered;
    }
}
