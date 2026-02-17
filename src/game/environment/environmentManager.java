package game.environment;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.collision.Manifold;
import org.jbox2d.callbacks.ContactImpulse;
import game.entities.platform;
import java.awt.Graphics;

public class environmentManager implements ContactListener {
    private World world;
    private Body playerBody;
    private Body floorBody, floor1Body, floor3Body;

    // Your EXISTING textured platforms
    private platform visualFloor;
    private platform visualFloor1;
    private platform visualFloor3;

    private static final float PIXELS_PER_METER = 50f;

    public environmentManager() {
        world = new World(new Vec2(0.0f, -10.0f));
        world.setContactListener(this);
        createPlayer();
        createFloors();
        createVisualPlatforms();
    }

    private void createPlayer() {
        BodyDef def = new BodyDef();
        def.type = BodyType.DYNAMIC;
        def.position.set(2.0f, 5.0f);
        playerBody = world.createBody(def);
        playerBody.setUserData("player");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.3f, 0.8f);
        playerBody.createFixture(shape, 1.0f);
    }

    private void createFloors() {
        // Physics bodies (invisible)
        floorBody = createPlatformBody(0f, -5.5f, 16f, 1f, "floor");
        floor1Body = createPlatformBody(-4f, -1.5f, 8f, 1f, "floor");
        floor3Body = createPlatformBody(-4f, 2.5f, 8f, 1f, "floor");
    }

    private Body createPlatformBody(float x, float y, float w, float h, String type) {
        BodyDef def = new BodyDef();
        def.type = BodyType.STATIC;
        def.position.set(x, y);
        Body body = world.createBody(def);
        body.setUserData(type);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/2, h/2);
        body.createFixture(shape, 0f);

        return body;
    }

    private void createVisualPlatforms() {
        // Your TEXTURED platforms - positions match physics
        visualFloor = new platform(0, 550, 800, 50);
        visualFloor1 = new platform(0, 350, 400, 50);
        visualFloor3 = new platform(0, 150, 400, 50);
    }

    public void update() {
        world.step(1.0f/60.0f, 6, 2);
    }

    public void render(Graphics g) {
        // Render YOUR textured platforms
        if (visualFloor != null) visualFloor.render(g);
        if (visualFloor1 != null) visualFloor1.render(g);
        if (visualFloor3 != null) visualFloor3.render(g);

        // Render player (red box for now)
        Vec2 pos = playerBody.getPosition();
        int screenX = 400 + (int)(pos.x * PIXELS_PER_METER);
        int screenY = 300 - (int)(pos.y * PIXELS_PER_METER);

        g.setColor(java.awt.Color.RED);
        g.fillRect(screenX - 15, screenY - 40, 30, 80);
    }

    @Override
    public void beginContact(Contact contact) {
        System.out.println("COLLISION!");
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
