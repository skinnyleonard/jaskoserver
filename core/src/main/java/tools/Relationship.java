package tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Relationship {
    public enum RelationshipType {
        CONFRONTED, PERPENDICULAR, NONE, PERPENDICULAR_LEFT, PERPENDICULAR_RIGHT;
    }

    public static RelationshipType classifyRelationship(Body a, Body b) {
        Vector2 dirA = new Vector2((float)Math.cos(a.getAngle()), (float)Math.sin(a.getAngle()));
        Vector2 dirB = new Vector2((float)Math.cos(b.getAngle()), (float)Math.sin(b.getAngle()));

        Vector2 delta = b.getPosition().cpy().sub(a.getPosition()).nor();

        float angleA = dirA.angleDeg(delta);
        float angleB = dirB.angleDeg(delta.cpy().scl(-1));
        float angleBetween = dirA.angleDeg(dirB);

        boolean aSeesB = angleA < 60f;
        boolean bSeesA = angleB < 60f;
        boolean facingEachOther = Math.abs(angleBetween - 180f) < 45f;

        if (aSeesB && bSeesA && facingEachOther) {
            return RelationshipType.CONFRONTED;
        }

        if (Math.abs(angleBetween - 90f) < 45f) {
            Vector2 lateral = new Vector2(dirA.y,dirA.x);

            float side = delta.dot(lateral);

            if(side > 0) {
                return RelationshipType.PERPENDICULAR_LEFT;
            } else {
                return RelationshipType.PERPENDICULAR_RIGHT;
            }
        }
        return RelationshipType.NONE;
    }
}
