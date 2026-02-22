package game;

import city.cs.engine.*;
import game.entities.deathBarrier;
import game.entities.platform;
import game.environment.environmentManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

import game.entities.cameraSensor;

public class gamePanel extends UserView {

    private environmentManager environment;
    private BufferedImage backgroundTexture;
    private boolean debugVisible = false;
    private long lastAlertTime = System.currentTimeMillis();
    private org.jbox2d.common.Vec2 cameraPos;
    private BufferedImage platformTexture;
    private newGame game;


    public gamePanel(environmentManager environment, newGame game) {
        super(environment, 800, 600);
        setZoom(35);//camera zoom
        this.environment = environment;
        setFocusable(true);
        requestFocusInWindow();
        org.jbox2d.common.Vec2 start = environment.getPlayerPosition();
        cameraPos = new org.jbox2d.common.Vec2(start.x, start.y);
        this.setCentre(cameraPos);

        URL texUrl = getClass().getResource("/textures/Game art_Platform 2.png");
        if (texUrl != null) {
            try {
                platformTexture = ImageIO.read(texUrl);
            } catch (IOException e) {
                platformTexture = null;
            }
        }

        URL bgUrl = getClass().getResource("/textures/Game art_Background 1.png");
        if (bgUrl != null) {
            try {
                backgroundTexture = ImageIO.read(bgUrl);
            } catch (IOException e) {
                backgroundTexture = null;
            }
        }

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (environment.getState() == environmentManager.GameState.TITLE) {
                        environment.startGame();
                    } else if (environment.getState() == environmentManager.GameState.WIN
                            || environment.getState() == environmentManager.GameState.LOSE) {
                        // restart whole game
                        game.restartGame();
                    }
                }

                if (environment.getState() != environmentManager.GameState.PLAYING)
                    return; // ignores other controls unless playing

                if (e.getKeyCode() == KeyEvent.VK_W)
                    environment.requestJump();
                if (e.getKeyCode() == KeyEvent.VK_A)
                    environment.setMoveLeft(true);
                if (e.getKeyCode() == KeyEvent.VK_D)
                    environment.setMoveRight(true);
                if (e.getKeyCode() == KeyEvent.VK_G)
                    environment.toggleDoubleJump();
                if (e.getKeyCode() == KeyEvent.VK_E)
                    environment.tryCollectLoot();
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A)
                    environment.setMoveLeft(false);
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D)
                    environment.setMoveRight(false);
            }
        });
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        if (backgroundTexture != null)
            g.drawImage(backgroundTexture, 0, 0, getWidth(), getHeight(), null);
    }

    public void updateCamera() {
        org.jbox2d.common.Vec2 target = environment.getPlayerPosition();
        float alpha = 0.1f;

        cameraPos.x += (target.x - cameraPos.x) * alpha;
        cameraPos.y += (target.y - cameraPos.y) * alpha;

        this.setCentre(cameraPos);
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        float zoom = getZoom();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        environmentManager.GameState state = environment.getState();
        long ms = environment.getElapsedTime();
        int seconds = (int) (ms / 1000);
        int globalAlert = environment.getGlobalAlertLevel();

        for (cameraSensor cam : environment.getCameras()) { //drawing camera beams individually
            int alertLevel = globalAlert;

            java.awt.Color beamColor = switch (alertLevel) {
                case 1 -> new java.awt.Color(255, 255, 0, 80);
                case 2 -> new java.awt.Color(255, 165, 0, 100);
                case 3 -> new java.awt.Color(255, 0, 0, 120);
                default -> new java.awt.Color(255, 255, 255, 60);
            };

            org.jbox2d.common.Vec2 camPosWorld = cam.getPosition();
            java.awt.geom.Point2D.Float camScreen = worldToView(camPosWorld);

            float beamLength = (cam.getBeamLength() + alertLevel) * zoom;

            float angle = cam.getSweepAngle();

            int camX = Math.round(camScreen.x);
            int camY = Math.round(camScreen.y);

            int x1 = (int) (camX + Math.cos(angle + 0.4f) * beamLength);
            int y1 = (int) (camY - Math.sin(angle + 0.4f) * beamLength);
            int x2 = (int) (camX + Math.cos(angle - 0.4f) * beamLength);
            int y2 = (int) (camY - Math.sin(angle - 0.4f) * beamLength);

            int[] xPoints = {camX, x1, x2};
            int[] yPoints = {camY, y1, y2};

            g.setColor(beamColor);
            g.fillPolygon(xPoints, yPoints, 3);
            g.setColor(beamColor.brighter());
            g.drawPolygon(xPoints, yPoints, 3);
        }

        // Draw death barriers
        for (deathBarrier db : environment.getDeathZones()) {
            org.jbox2d.common.Vec2 pos = db.getPosition();
            java.awt.geom.Point2D.Float screen = worldToView(pos);

            float worldW = db.getHalfWidth() * 2f;
            float worldH = db.getHalfHeight() * 2f;

            int pxW = Math.round(worldW * zoom);
            int pxH = Math.round(worldH * zoom);

            int drawX = Math.round(screen.x - pxW / 2f);
            int drawY = Math.round(screen.y - pxH / 2f);

            g.setColor(db.getColor());
            g.fillRect(drawX, drawY, pxW, pxH);
        }


        for (StaticBody body : environment.getPlatforms()) {
            if (!(body instanceof platform) || platformTexture == null) continue;

            platform p = (platform) body;

            // world centre of the platform
            org.jbox2d.common.Vec2 pos = p.getPosition();

            // convert world to screen
            java.awt.geom.Point2D.Float screen = worldToView(pos);

            float worldW = p.getHalfWidth() * 2f;
            float worldH = p.getHalfHeight() * 2f;

            // convert to pixels
            int pxW = Math.round(worldW * zoom);
            int pxH = Math.round(worldH * zoom);

            int drawX = Math.round(screen.x - pxW / 2f);
            int drawY = Math.round(screen.y - pxH / 2f);

            g.drawImage(platformTexture, drawX, drawY, pxW, pxH, null);
        }

        // Show collect loot prompt
        if (environment.getLootInRange() != null) {
            // screen position of player
            org.jbox2d.common.Vec2 playerPos = environment.getPlayerPosition();
            java.awt.geom.Point2D.Float playerScreen = worldToView(playerPos);

            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            g.drawString("Press E to collect",
                    (int) playerScreen.x - 50,
                    (int) playerScreen.y - 40);
        }

        // HUD
        if (state == environmentManager.GameState.PLAYING) {
            int secondsLeft = (int) (environment.getAlertTimeRemaining() / 1000);
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
            g.drawString("Lives: " + environment.getPlayer().getLives(), 20, 30);
            g.drawString("Loot: " + environment.getPlayer().getLoot(), 20, 55);
            g.setColor(java.awt.Color.RED);
            g.drawString("Alert: " + globalAlert + "/3", 20, 80);
            if (globalAlert > 0) {
                g.drawString("Decays in: " + secondsLeft + "s", 20, 105);
            }
        }

        if (state == environmentManager.GameState.TITLE) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("CCTV HEIST", getWidth() / 2 - 110, getHeight() / 2 - 40);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String line1 = "Press ENTER to start";
            String line2 = "Controls: A/D move, W jump, E collect, G toggle double jump";
            String line3 = "Avoid cameras when alert is red. Collect loot. Reach the exit.";
            FontMetrics fm = g.getFontMetrics();
            int y = getHeight() / 2;
            g.drawString(line1, (getWidth() - fm.stringWidth(line1)) / 2, y);
            g.drawString(line2, (getWidth() - fm.stringWidth(line2)) / 2, y + 25);
            g.drawString(line3, (getWidth() - fm.stringWidth(line3)) / 2, y + 50);
        }


        if (state == environmentManager.GameState.WIN) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("HEIST COMPLETE!", getWidth() / 2 - 150, getHeight() / 2 - 60);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Loot: " + environment.getPlayer().getLoot(), getWidth() / 2 - 70, getHeight() / 2 - 20);
            g.drawString("Time: " + seconds + "s", getWidth() / 2 - 70, getHeight() / 2 + 10);
            g.drawString("Lives left: " + environment.getPlayer().getLives(), getWidth() / 2 - 70, getHeight() / 2 + 40);
            g.drawString("Press ENTER to restart", getWidth() / 2 - 120, getHeight() / 2 + 80);
        }

        if (state == environmentManager.GameState.LOSE) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("YOU WERE CAUGHT!", getWidth() / 2 - 160, getHeight() / 2 - 40);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Loot: " + environment.getPlayer().getLoot(), getWidth() / 2 - 70, getHeight() / 2);
            g.drawString("Time: " + seconds + "s", getWidth() / 2 - 70, getHeight() / 2 + 30);
            g.drawString("Press ENTER to restart", getWidth() / 2 - 120, getHeight() / 2 + 70);
        }
    }


}
