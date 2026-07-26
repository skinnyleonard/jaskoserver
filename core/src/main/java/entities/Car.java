package entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import tools.Constants;
import tools.Loader;
import tools.MapLoader;

public class Car extends BodyHolder {
    public static final int DRIVE_2WD = 0;
    public static final int DRIVE_4WD = 1;

    public static final int DRIVE_DIRECTION_NONE = 0;
    public static final int DRIVE_DIRECTION_FORWARD = 1;
    public static final int DRIVE_DIRECTION_BACKWARD = 2;

    public static final int TURN_DIRECTION_NONE = 0;
    public static final int TURN_DIRECTION_LEFT = 1;
    public static final int TURN_DIRECTION_RIGHT = 2;

    private static final Vector2 WHEEL_SIZE = new Vector2(16, 32);
    private static final float LINEAR_DAMPING = 0.5f;
    private static final float RESTITUTION = 0.2f;

    private static final float MAX_WHEEL_ANGLE = 20.0f;
    private static final float WHEEL_TURN_INCREMENT = 1.0f;

    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;

    private float mCurrentWheelAngle = 0;
    public final Array<Wheel> mAllWheels = new Array<Wheel>();
    private final Array<Wheel> mRevolvingWheels = new Array<Wheel>();
    private float drift;
    private float mCurrentMaxSpeed;
    private final float mRegularMaxSpeed;
    private float aceleration;
    public int imageIterationNumber = 1;
    public boolean flip = false;
    public String carBrand = "";
    public int id;

    public Car(final float maxSpeed, final float drift, final float aceleration, final Loader mapLoader, int wheelDrive, World world, float x, float y, int user, String carBrand) {
        super(mapLoader.placePlayer(x, y, user+1));
        this.id = user;
        System.out.println(id);
        this.carBrand = carBrand;
        this.mRegularMaxSpeed = maxSpeed;
        this.drift = drift;
        this.aceleration = aceleration;
        getBody().setLinearDamping(LINEAR_DAMPING);
        getBody().getFixtureList().get(0).setRestitution(RESTITUTION);
        createWheels(world, wheelDrive);
        getBody().getFixtureList().get(0);
    }

    public String getMetrics() {
        float x = getBody().getPosition().x*Constants.PPM;
        float y = getBody().getPosition().y*Constants.PPM;
        float angle = getBody().getAngle();
        angle = (float)((angle % (2 * Math.PI) + (2 * Math.PI)) % (2 * Math.PI));

        return x + "%" + y + "%" + angle;
    }

    private void createWheels(World world, int wheelDrive) {
        for (int i = 0; i < 4; i++) {
            float xOffset = 0;
            float yOffset = 0;

            switch (i) {
                case Wheel.UPPER_LEFT:
                    xOffset = -64;
                    yOffset = 80;
                    break;
                case Wheel.UPPER_RIGHT:
                    xOffset = 64;
                    yOffset = 80;
                    break;
                case Wheel.DOWN_LEFT:
                    xOffset = -64;
                    yOffset = -80;
                    break;
                case Wheel.DOWN_RIGHT:
                    xOffset = 64;
                    yOffset = -80;
                    break;
                default:
            }
            boolean powered = wheelDrive == DRIVE_4WD || (wheelDrive == DRIVE_2WD && i < 2);

            Wheel wheel = new Wheel(
                new Vector2(getBody().getPosition().x * Constants.PPM + xOffset, getBody().getPosition().y * Constants.PPM + yOffset),
                WHEEL_SIZE, BodyDef.BodyType.DynamicBody, world, 0.4f, i, this, powered);
            if(i < 2) {
                RevoluteJointDef jointDef = new RevoluteJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody().getWorldCenter());
                jointDef.enableMotor = false;
                world.createJoint(jointDef);
            } else {
                PrismaticJointDef jointDef = new PrismaticJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody().getWorldCenter(), new Vector2(1, 0));
                jointDef.enableLimit = true;
                jointDef.lowerTranslation = jointDef.upperTranslation = 0;
                world.createJoint(jointDef);
            }

            mAllWheels.add(wheel);
            if(i < 2) {
                mRevolvingWheels.add(wheel);
            }
            wheel.setDrift(this.drift);
        }
    }

    private void processInput() {
        Vector2 baseVector = new Vector2(0, 0);

        if(mTurnDirection == TURN_DIRECTION_LEFT) {
            if(mCurrentWheelAngle < 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.min(mCurrentWheelAngle += WHEEL_TURN_INCREMENT, MAX_WHEEL_ANGLE);
        } else if(mTurnDirection == TURN_DIRECTION_RIGHT) {
            if(mCurrentWheelAngle > 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.max(mCurrentWheelAngle -= WHEEL_TURN_INCREMENT, -MAX_WHEEL_ANGLE);
        } else {
            mCurrentWheelAngle = 0;
        }

        for(Wheel wheel : new Array.ArrayIterator<Wheel>(mRevolvingWheels)) {
            wheel.setAngle(mCurrentWheelAngle);
        }

        if(mDriveDirection == DRIVE_DIRECTION_FORWARD) {
            baseVector.set(0, this.aceleration);
        } else if(mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            if(direction() == DIRECTION_BACKWARD) {
                baseVector.set(0, -this.aceleration * 0.7f);
            } else if (direction() == DIRECTION_FORWARD) {
                baseVector.set(0, -this.aceleration * 1.3f);
            } else {
                baseVector.set(0, -this.aceleration);
            }
        }

        if(direction() == DRIVE_DIRECTION_BACKWARD) {
            mCurrentMaxSpeed = mRegularMaxSpeed/2;
        } else {
            mCurrentMaxSpeed = mRegularMaxSpeed;
        }

        if(getBody().getLinearVelocity().len() < mCurrentMaxSpeed) {
            for(Wheel wheel : new Array.ArrayIterator<Wheel>(mAllWheels)) {
                if(wheel.isPowered()) {
                    wheel.getBody().applyForceToCenter(wheel.getBody().getWorldVector(baseVector), true);
                }
            }
        }
    }

    public void setDriveDirection(int driveDirection) {
        this.mDriveDirection = driveDirection;
    }

    public void setTurnDirection(int turnDirection) {
        this.mTurnDirection = turnDirection;
    }

//    private void getData() {
//        if(super.getLateralVelocity().len() > 10) {
//            System.out.println(super.getLateralVelocity().len());
//        }
//    }

    @Override
    public void update(float delta) {
//        getData();
        super.update(delta);
        processInput();
        for(Wheel wheel : new Array.ArrayIterator<Wheel>(mAllWheels)) {
            wheel.update(delta);
        }
    }
}
