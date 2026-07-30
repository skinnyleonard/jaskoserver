package tools;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Loader {
    private static final String MAP_WALL = "wall";
    private static final String MAP_CHECK = "checkpoints";
    private World world;

//    public ArrayList<PointMapObject> positions = new ArrayList<PointMapObject>();
    public ArrayList<Vector2> positions = new ArrayList<Vector2>();
    public static int maxCheck = 0;

    public Loader(World world) throws ParserConfigurationException, IOException, SAXException {
        this.world = world;

        File xmlFile = new File("assets/mapa_demo_final.tmx");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("objectgroup");
        for(int i = 0; i < nList.getLength(); i++){  //aca parseo los <objectgroup>
            Node node  = nList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) node;
                System.out.println("objectgroup name: "+element.getAttribute("name"));
                NodeList objects = element.getElementsByTagName("object");
                for(int j = 0; j < objects.getLength(); j++){  //aca parseo por los <object>
                    Node objectNode = objects.item(j);
                    if(objectNode.getNodeType() == Node.ELEMENT_NODE){

                        Element objectElement = (Element) objectNode; //este el objeto a analizar dentro de objectgroup
//                        Node parent = objectElement.getParentNode(); //este es el padre de la gordita
//                        Element parentElement = (Element) parent;

                        handleChildObjects(objectElement, element);

//                        System.out.println("    padre: "+parentElement.getAttribute("name")+
//                            "    name: "+objectElement.getAttribute("name")+
//                            " x: "+objectElement.getAttribute("x")+
//                            " y: "+objectElement.getAttribute("y"));
                    }
                }
            }
        }
    }

    private void handleChildObjects(Element objectElement, Element parentElement) {
        if(parentElement.getAttribute("name").equals(MAP_WALL)) { //aca obtengo los polylines dentro de object
            for(int k = 0; k < objectElement.getChildNodes().getLength(); k++) {
                Node childNode = objectElement.getChildNodes().item(k);
                if(childNode.getNodeType() == Node.ELEMENT_NODE){
                    ShapeFactory.createPolyline(
                        parsePolylinePoints(objectElement, childNode), BodyDef.BodyType.StaticBody, this.world, 1f, false, "pared"
                    );
                }
            }
        }

        if(parentElement.getAttribute("name").equals("grid")) {
            for(int k = 0; k < objectElement.getChildNodes().getLength(); k++) {
                Node childNode = objectElement.getChildNodes().item(k);
                if(childNode.getNodeType() == Node.ELEMENT_NODE){
                    Element childElement = (Element) childNode; //aca obtengo el <polyline>

                    positions.add(new Vector2(Float.parseFloat(objectElement.getAttribute("x")),
                        6912-(Float.parseFloat(objectElement.getAttribute("y")))));
                }
            }
        }

        if(parentElement.getAttribute("name").equals(MAP_CHECK)) { //aca obtengo los polylines dentro de object
            for(int k = 0; k < objectElement.getChildNodes().getLength(); k++) {
                Node childNode = objectElement.getChildNodes().item(k);
                if(childNode.getNodeType() == Node.ELEMENT_NODE){
                    String name = objectElement.getAttribute("name");
                    maxCheck = Integer.parseInt(name.replaceAll("[^0-9]", ""));

                    ShapeFactory.createCheck(
                        parsePolylinePoints(objectElement, childNode), BodyDef.BodyType.StaticBody, world, 1f, true, name
                    );
                }
            }
        }
    }

    private Vector2[] parsePolylinePoints(Element objectElement, Node childNode) {
        Element childElement = (Element) childNode; //aca obtengo el <polyline>

        String[] vertices = childElement.getAttribute("points").split(" ");
        float baseX = Float.parseFloat(objectElement.getAttribute("x"));
        float baseY = Float.parseFloat(objectElement.getAttribute("y"));

        Vector2[] worldVertices = new Vector2[vertices.length];
        for (int l = 0; l < vertices.length; l++) {
            String[] xy = vertices[l].split(",");
            float x = baseX+Float.parseFloat(xy[0]);
            float y = 6912-(baseY+Float.parseFloat(xy[1]));
            worldVertices[l] = new Vector2(x / Constants.PPM, y / Constants.PPM);
        }

        return worldVertices;
    }

    public Body placePlayer(float x, float y, int user) {
        return ShapeFactory.createPlayer(
            new Vector2(x + (float) 128 / 2, y - (float) 256 / 2),
            new Vector2((float) 128 / 2, (float) 256 / 2),
            BodyDef.BodyType.DynamicBody, world, 0.4f, false, ("car"+user));
    }
}
