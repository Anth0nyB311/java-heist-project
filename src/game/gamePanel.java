package game;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import game.environment.environmentManager;

public class gamePanel extends JPanel {

    private environmentManager environment;
    private BufferedImage backgroundTexture;


    public gamePanel(environmentManager environment) {
        this.environment = environment;

        // Load from classpath: src/resources/textures/Game art_Background 1.png  ->  "/textures/Game art_Background 1.png"
        URL bgUrl = getClass().getResource("/textures/Game art_Background 1.png");
        if (bgUrl == null) {
            backgroundTexture = null;
        } else {
            try {
                backgroundTexture = ImageIO.read(bgUrl);
                if (backgroundTexture == null) {
                }
            } catch (IOException e) {
                backgroundTexture = null;
            }
        }

        setBackground(new Color(2, 2, 2)); // fallback
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundTexture != null) {
            int imgW = backgroundTexture.getWidth();
            int imgH = backgroundTexture.getHeight();
            int panelW = getWidth();
            int panelH = getHeight();

            double scaleW = (double) panelW / imgW;
            double scaleH = (double) panelH / imgH;
            double scale = Math.min(scaleW, scaleH); // fit without stretch

            int scaledW = (int) (imgW * scale);
            int scaledH = (int) (imgH * scale);
            int x = (panelW - scaledW) / 2; // center horizontally
            int y = (panelH - scaledH) / 2; // center vertically

            g.drawImage(backgroundTexture, x, y, scaledW, scaledH, this);
        }

        if (environment != null) {
            environment.render(g); // renders physics bodies
        }

        g.setColor(Color.WHITE);
        g.drawString("CCTV Heist - Part 1 Skeleton", 20, 20);
    }

}
