package game.entities;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import city.cs.engine.Walker;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

import java.net.URL;

public class enemy extends Walker {

    private static final Shape SHAPE = new BoxShape(0.4f, 0.8f);

    private float leftX;
    private float rightX;
    private float baseSpeed;
    private int direction = 1; // 1 = right, -1 = left
    private static final String ENEMY_TEXTURE_PATH = "/textures/enemy.png";

    public enemy(World world,
                 float startX, float startY,
                 float leftX, float rightX,
                 float baseSpeed) {

        super(world, SHAPE);
        setPosition(new Vec2(startX, startY));

        this.leftX = leftX;
        this.rightX = rightX;
        this.baseSpeed = baseSpeed;

        URL textureUrl = enemy.class.getResource(ENEMY_TEXTURE_PATH);
        if (textureUrl == null) {
            throw new IllegalStateException("Missing resource: " + ENEMY_TEXTURE_PATH);
        }
        addImage(new BodyImage(textureUrl, 2f));
    }

    public void updateSpeed(int alertLevel) {
        float speed = baseSpeed * (1.0f + 0.5f * alertLevel);
        startWalking(direction * speed);
    }

    public void patrol() {
        float x = getPosition().x;
        if (x < leftX && direction < 0) {
            direction = 1;
            startWalking(Math.abs(getLinearVelocity().x));
        } else if (x > rightX && direction > 0) {
            direction = -1;
            startWalking(-Math.abs(getLinearVelocity().x));
        }
    }

    public void kill() {
        destroy();
    }
}