package game.entities;

import city.cs.engine.BodyImage;
import city.cs.engine.CircleShape;
import city.cs.engine.Shape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

import java.net.URL;

public class loot extends StaticBody {

    private static final Shape SHAPE = new CircleShape(0.3f);
    private static final String LOOT_TEXTURE_PATH = "/textures/loot.png";

    public loot(World world, float x, float y) {
        super(world, SHAPE);
        setPosition(new Vec2(x, y));

        URL textureUrl = loot.class.getResource(LOOT_TEXTURE_PATH);
        if (textureUrl == null) {
            throw new IllegalStateException("Missing resource: " + LOOT_TEXTURE_PATH);
        }
        addImage(new BodyImage(textureUrl, 1.5f));
    }
}