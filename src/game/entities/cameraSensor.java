package game.entities;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.PolygonShape;
import city.cs.engine.Sensor;
import city.cs.engine.Shape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

import java.net.URL;
import java.util.Arrays;

public class cameraSensor extends StaticBody {

    private float sweepAngle;
    private float sweepSpeed = 0.01f;
    private float maxSweep = (float) Math.PI / 4;
    private boolean goingRight;
    private Sensor sensor;
    private float beamLength;
    private float centreAngle;

    private static final Shape SHAPE = new BoxShape(0.3f, 0.3f);
    private static final String CAM_TEXTURE_PATH = "/textures/SecurityCam.png";

    public cameraSensor(World world, float x, float y, float beamLength, float startAngle, boolean startGoingRight) {
        super(world, SHAPE);
        setPosition(new Vec2(x, y));
        this.beamLength = beamLength;
        this.goingRight = startGoingRight;
        this.sweepAngle = startAngle;
        this.centreAngle = startAngle;

        Vec2[] cone = {
                new Vec2(0f, 0f),
                new Vec2(beamLength, beamLength * 0.4f),
                new Vec2(beamLength, -beamLength * 0.4f)
        };
        sensor = new Sensor(this, new PolygonShape(Arrays.asList(cone)));

        URL camUrl = cameraSensor.class.getResource(CAM_TEXTURE_PATH);
        if (camUrl == null) {
            throw new IllegalStateException("Missing resource: " + CAM_TEXTURE_PATH);
        }
        addImage(new BodyImage(camUrl, 1f));
    }

    public Sensor getSensor() {
        return sensor;
    }

    public float getBeamLength() {
        return beamLength;
    }

    public void update() {
        if (goingRight) {
            sweepAngle += sweepSpeed;
            if (sweepAngle >= centreAngle + maxSweep) goingRight = false;
        } else {
            sweepAngle -= sweepSpeed;
            if (sweepAngle <= centreAngle - maxSweep) goingRight = true;
        }
        setAngle(sweepAngle);
    }

    public float getSweepAngle() {
        return sweepAngle;
    }
}