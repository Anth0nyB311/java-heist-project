package game.entities;

import city.cs.engine.BoxShape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class platform extends StaticBody {

    private final float halfWidth;
    private final float halfHeight;

    public platform(World world, float x, float y, float halfWidth, float halfHeight) {
        super(world, new BoxShape(halfWidth, halfHeight));
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        setPosition(new Vec2(x, y));
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }
}