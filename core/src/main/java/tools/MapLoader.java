package tools;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import screens.PlayScreen;

import java.util.ArrayList;

public class MapLoader implements Disposable {
    private static final String MAP_WALL = "wall";
    private static final String MAP_PLAYER = "player";
    private static final String MAP_CHECK = "checkpoints";
    public static int maxCheck = 0;

    private final World mWorld;
    private final TiledMap mMap;
    public ArrayList<PointMapObject> positions = new ArrayList<PointMapObject>();

    public MapLoader(World world) {
        this.mWorld = world;
        mMap = new TmxMapLoader().load(Constants.MAP_NAME);
//        mMap = new TmxMapLoader(new InternalFileHandleResolver()).load(Constants.MAP_NAME);
        //final Array<RectangleMapObject> walls = mMap.getLayers().get(MAP_WALL).getObjects().getByType(RectangleMapObject.class);
        for (MapObject rObject : mMap.getLayers().get(MAP_WALL).getObjects()) {
            if(rObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) rObject).getRectangle();
                ShapeFactory.createRectangle(
                    new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2),
                    new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2),
                    BodyDef.BodyType.StaticBody, mWorld, 1f, false);
            }

            if(rObject instanceof PolygonMapObject) {
                float[] vertices = ((PolygonMapObject) rObject).getPolygon().getTransformedVertices();
                float[] worldVertices = new float[vertices.length];

                for (int i = 0; i < vertices.length; i++) {
                    worldVertices[i] = vertices[i] / Constants.PPM;
                }
                ShapeFactory.createPolygon(worldVertices, BodyDef.BodyType.StaticBody, mWorld, 1f, false);
            }

            if(rObject instanceof PolylineMapObject) {
                float[] vertices = ((PolylineMapObject)rObject).getPolyline().getTransformedVertices();
                Vector2[] worldVertices = new Vector2[vertices.length / 2];

                for (int i = 0; i < vertices.length / 2; i++) {
                    worldVertices[i] = new Vector2();
                    worldVertices[i].x = vertices[i*2]/Constants.PPM;
                    worldVertices[i].y = vertices[i*2+1]/Constants.PPM;
                }
                ShapeFactory.createPolyline(
                    worldVertices, BodyDef.BodyType.StaticBody, world, 1f, false, "pared"
                );
            }
        }
        for(MapObject rObject : mMap.getLayers().get("grid").getObjects()) {
            if(rObject instanceof PointMapObject) {
                PointMapObject point = (PointMapObject) rObject;
                positions.add(point);
            }
        }

        for (MapObject rObject : mMap.getLayers().get("checkpoints").getObjects()) {
            if(rObject instanceof PolylineMapObject) {
                String name = rObject.getName();
                System.out.println(name + " cargado...");
                maxCheck = Integer.parseInt(name.replaceAll("[^0-9]", ""));

                //  HUD.checkLabel.setText(WorldContactListener.jugadorCount [Integer.parseInt(WorldContactListener.car.replaceAll("[^0-9]", ""))][0] + " / " + MapLoader.maxCheck );
                //  HUD.lapLabel.setText(WorldContactListener.jugadorCount [Integer.parseInt(WorldContactListener.car.replaceAll("[^0-9]", ""))][1] + " / " + WorldContactListener.maxLap);

                float[] vertices = ((PolylineMapObject)rObject).getPolyline().getTransformedVertices();
                Vector2[] worldVertices = new Vector2[vertices.length / 2];

                for (int i = 0; i < vertices.length / 2; i++) {
                    worldVertices[i] = new Vector2();
                    worldVertices[i].x = vertices[i*2]/Constants.PPM;
                    worldVertices[i].y = vertices[i*2+1]/Constants.PPM;
                }
                ShapeFactory.createCheck(
                    worldVertices, BodyDef.BodyType.StaticBody, world, 1f, true, name
                );



            }
        }

//        for(MapLayer rObject : mMap.getLayers()) {
//            if(rObject instanceof TiledMapImageLayer) {
//                TiledMapImageLayer textureObj = (TiledMapImageLayer) rObject;
//                float x = textureObj.getX() * Constants.PPM;
//                System.out.println(textureObj.getTextureRegion().getRegionWidth() + "x" + textureObj.getTextureRegion().getRegionHeight());
//                float y = textureObj.getY() * Constants.PPM;
//                float width = textureObj.getTextureRegion().getRegionWidth() * Constants.PPM;
//                float height = textureObj.getTextureRegion().getRegionHeight() * Constants.PPM;
//                PlayScreen.batch.begin();
//                PlayScreen.batch.draw(textureObj.getTextureRegion(), x, y, width, height);
//                PlayScreen.batch.end();
//            }
//        }
    }

    public Body placePlayer(float x, float y, int user) {
        return ShapeFactory.createPlayer(
            new Vector2(x + (float) 128 / 2, y - (float) 256 / 2),
            new Vector2((float) 128 / 2, (float) 256 / 2),
            BodyDef.BodyType.DynamicBody, mWorld, 0.4f, false, ("car"+user));
    }

//    public Body getPlayers() {
//        final Rectangle rectangle = mMap.getLayers().get(MAP_PLAYER).getObjects().getByType(RectangleMapObject.class).get(0).getRectangle();
//        return ShapeFactory.createPlayer(
//            new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2),
//            new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2),
//            BodyDef.BodyType.DynamicBody, mWorld, 0.4f, false);
//    }

    @Override
    public void dispose() {
        mMap.dispose();
    }
}
