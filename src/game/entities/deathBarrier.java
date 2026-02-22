package game.entities;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class deathBarrier extends StaticBody {

    private java.awt.Color color;
    private Vec2 respawnPos;
    private float halfWidth, halfHeight;

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public deathBarrier(World world, float x, float y,
                        float halfWidth, float halfHeight,
                        java.awt.Color color, Vec2 respawnPos) {
        super(world, new BoxShape(halfWidth, halfHeight));
        this.color = color;
        this.respawnPos = respawnPos;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        setPosition(new Vec2(x, y));
    }

    public java.awt.Color getColor() {
        return color;
    }

    public Vec2 getRespawnPos() {
        return respawnPos;
    }
}

