package game.entities;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import city.cs.engine.Walker;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

import java.net.URL;

public class player extends Walker {

    private static final Shape SHAPE = new BoxShape(0.4f, 0.9f);
    private int lives = 3;
    private int loot = 0;
    private int jumpCount = 0;
    private boolean doubleJumpEnabled = true;  // toggle this
    private static final String PLAYER_TEXTURE_PATH = "/textures/player.png";

    public player(World world) {
        super(world, SHAPE);
        setPosition(new Vec2(0f, 2.5f));

        URL textureUrl = player.class.getResource(PLAYER_TEXTURE_PATH);
        if (textureUrl == null) {
            throw new IllegalStateException("Missing resource: " + PLAYER_TEXTURE_PATH);
        }
        addImage(new BodyImage(textureUrl, 2f));
    }

    public void doJump() {
        int maxJumps = doubleJumpEnabled ? 2 : 1;
        if (jumpCount < maxJumps) {
            Vec2 vel = getLinearVelocity();
            vel.y = 8f;
            setLinearVelocity(vel);
            jumpCount++;
        }
    }

    public void resetJumps() {
        jumpCount = 0;
    }

    public void toggleDoubleJump() {
        doubleJumpEnabled = !doubleJumpEnabled;
    }

    public boolean isDoubleJumpEnabled() {
        return doubleJumpEnabled;
    }

    public int getLives() {
        return lives;
    }

    public int getLoot() {
        return loot;
    }

    public void loseLife() {
        lives--;
    }

    public void collectLoot(int amount) {
        loot += amount;
    }
}