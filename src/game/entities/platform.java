package game.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.io.IOException;
import javax.imageio.ImageIO;

public class platform {

    private int x, y, width, height;
    private Image texture;   // optional texture; null means fallback to color

    public platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        try {
            // path relative to resources root, e.g. /textures/platform.png
            texture = ImageIO.read(
                    getClass().getResource("/textures/Game art_Platform 1.png")
            );
        } catch (IOException | IllegalArgumentException e) {
            texture = null; // fallback if missing
        }
    }

    public void render(Graphics g) {
        if (texture != null) {
            g.drawImage(texture, x, y, width, height, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
        }
    }
}
