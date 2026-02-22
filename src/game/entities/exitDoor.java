package game.entities;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.net.URL;

public class exitDoor extends StaticBody {

    private static final Shape SHAPE = new BoxShape(0.5f, 1.2f);
    private static final String DOOR_TEXTURE_PATH = "/textures/exitDoor.png";


    public exitDoor(World world, float x, float y) {
        super(world, SHAPE);
        setPosition(new Vec2(x, y));
        URL textureUrl = enemy.class.getResource(DOOR_TEXTURE_PATH);
        if (textureUrl == null) {
            throw new IllegalStateException("Missing resource: " + DOOR_TEXTURE_PATH);
        }
        addImage(new BodyImage(textureUrl, 3f));
    }
}
