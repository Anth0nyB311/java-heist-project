package game.environment;

import city.cs.engine.Body;
import city.cs.engine.BoxShape;
import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
import city.cs.engine.Sensor;
import city.cs.engine.SensorEvent;
import city.cs.engine.SensorListener;
import city.cs.engine.StaticBody;
import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import city.cs.engine.World;
import game.entities.cameraSensor;
import game.entities.deathBarrier;
import game.entities.enemy;
import game.entities.exitDoor;
import game.entities.loot;
import game.entities.platform;
import game.entities.player;
import game.gamePanel;
import org.jbox2d.common.Vec2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class environmentManager extends World implements CollisionListener {

    public enum GameState {TITLE, PLAYING, WIN, LOSE}

    private GameState state = GameState.TITLE;
    private long startTime = 0;
    private long elapsedTime = 0;  // ms

    private player playerEntity;

    private final List<StaticBody> platforms = new ArrayList<>();
    private final List<cameraSensor> cameras = new ArrayList<>();

    private javax.swing.Timer alertDecayTimer;
    private int alertDecayDelay = 8000; // 3 seconds before decay

    private gamePanel view;

    private final List<deathBarrier> deathZones = new ArrayList<>();

    private final List<loot> lootItems = new ArrayList<>();
    private loot lootInRange = null;

    public loot getLootInRange() {
        return lootInRange;
    }

    private int globalAlertLevel = 0;

    private final List<enemy> enemies = new ArrayList<>();
    private exitDoor door;

    private final List<enemy> enemiesToRemove = new ArrayList<>();
    private long nextDecayTime = 0;

    public environmentManager() {
        //platforms
        platforms.add(new platform(this, -10f, -8f, 8f, 0.5f));
        platforms.add(new platform(this, 4f, -2f, 6f, 0.5f));
        platforms.add(new platform(this, -10f, 3f, 4f, 0.5f));
        platforms.add(new platform(this, 0f, 7f, 5f, 0.5f));
        platforms.add(new platform(this, 4f, 13f, 1f, 0.5f));
        platforms.add(new platform(this, 20f, 15f, 4f, 0.5f));
        platforms.add(new platform(this, 7f, 19f, 1f, 0.5f));
        platforms.add(new platform(this, -5f, 23f, 6f, 0.5f));

        //death barriers
        deathZones.add(new deathBarrier(this, 4f, -70f, 100f, 50f, Color.BLACK, new Vec2(-9f, -6f)));
        setGravity(15f);

        //loot items
        lootItems.add(new loot(this, -5f, -6.5f));
        lootItems.add(new loot(this, 7f, -0.5f));
        lootItems.add(new loot(this, 0f, 8.5f));
        lootItems.add(new loot(this, 22f, 16.5f));

        //cameras
        cameraSensor cam1 = new cameraSensor(this, 3f, 5f, 6f, 11f, true);
        cameras.add(cam1);
        cameraSensor cam2 = new cameraSensor(this, -5f, 14f, 10f, 11f, false);
        cameras.add(cam2);
        cameraSensor cam3 = new cameraSensor(this, 7f, 18f, 4f, 12f, true);
        cameras.add(cam3);
        cameraSensor cam4 = new cameraSensor(this, 0f, 30f, 5f, 12f, true);
        cameras.add(cam4);

        // enemies
        enemies.add(new enemy(this, -10f, 3.5f, -13f, -9f, 2f));
        enemies.add(new enemy(this, -4f, 23.5f, -6f, 0f, 4f));

        //player
        playerEntity = new player(this);
        playerEntity.setPosition(new Vec2(-9f, -6f));
        playerEntity.addCollisionListener(this);

        // exit door
        door = new exitDoor(this, -8f, 25f);

        for (cameraSensor cam : cameras) {
            cam.getSensor().addSensorListener(new SensorListener() {
                @Override
                public void beginContact(SensorEvent e) {
                    if (e.getContactBody() == playerEntity) {
                        if (globalAlertLevel < 4) globalAlertLevel++;
                        alertDecayTimer.restart();
                        nextDecayTime = System.currentTimeMillis() + alertDecayDelay;
                        alertDecayTimer.start();

                        if (globalAlertLevel > 3) {
                            bustedByCamera();
                        }
                    }

                }

                @Override
                public void endContact(SensorEvent e) {
                }
            });
        }

        Sensor doorSensor = new Sensor(door, new BoxShape(0.7f, 1.5f));
        doorSensor.addSensorListener(new SensorListener() {
            @Override
            public void beginContact(SensorEvent e) {
                if (e.getContactBody() == playerEntity &&
                        state == GameState.PLAYING) {
                    winGame();
                }
            }

            @Override
            public void endContact(SensorEvent e) {
            }
        });

        addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent e) {
                if (state == GameState.PLAYING) {
                    for (cameraSensor cam : cameras) cam.update();

                    int alert = globalAlertLevel;
                    for (enemy en : enemies) {
                        en.updateSpeed(alert);
                        en.patrol();
                    }
                }
                updateViewCamera();
            }

            @Override
            public void postStep(StepEvent e) {
                if (!enemiesToRemove.isEmpty()) {
                    for (enemy en : enemiesToRemove) {
                        en.kill();           // destroy body
                        enemies.remove(en);  // remove from list
                    }
                    enemiesToRemove.clear();
                }
            }
        });

        alertDecayTimer = new javax.swing.Timer(alertDecayDelay, e -> {
            if (globalAlertLevel > 0) {
                globalAlertLevel--;

                if (globalAlertLevel > 0) {
                    // start a fresh countdown for the NEXT level
                    nextDecayTime = System.currentTimeMillis() + alertDecayDelay;
                    alertDecayTimer.restart();
                } else {
                    // no more decay scheduled
                    nextDecayTime = 0;
                    alertDecayTimer.stop();
                }
            } else {
                // safety: if already 0, stop timer
                nextDecayTime = 0;
                alertDecayTimer.stop();
            }
        });
        alertDecayTimer.setRepeats(true);
        alertDecayTimer.start();

    }

    public long getAlertTimeRemaining() {
        if (globalAlertLevel <= 0 || nextDecayTime == 0) return 0;
        long remaining = nextDecayTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    @Override
    public void collide(CollisionEvent e) {
        if (e.getReportingBody() != playerEntity && e.getOtherBody() != playerEntity) {
            return;
        }

        // Any contact involving the player resets jumps (landing / wall contact etc.)
        playerEntity.resetJumps();

        Body other = (e.getReportingBody() == playerEntity)
                ? e.getOtherBody()
                : e.getReportingBody();

        lootInRange = null;

        if (other instanceof loot l) {
            // marking that we are next to this loot
            lootInRange = l;
        }

        if (other instanceof deathBarrier db) {
            playerEntity.loseLife();
            if (playerEntity.getLives() <= 0) {
                loseGame();
                return;
            }
            playerEntity.setPosition(db.getRespawnPos());
            playerEntity.setLinearVelocity(new Vec2(0, 0));
            playerEntity.resetJumps();
        }

        if (other instanceof enemy en) {
            float playerY = playerEntity.getPosition().y;
            float enemyY = en.getPosition().y;

            if (playerY > enemyY + 0.3f &&
                    playerEntity.getLinearVelocity().y < 0) {
                enemiesToRemove.add(en);
                Vec2 vel = playerEntity.getLinearVelocity();
                vel.y = 8f;
                playerEntity.setLinearVelocity(vel);
                playerEntity.resetJumps();
            } else {
                playerEntity.loseLife();
                if (playerEntity.getLives() <= 0) {
                    loseGame();
                    return;
                }
                playerEntity.setPosition(new Vec2(-9f, -6f));
                playerEntity.setLinearVelocity(new Vec2(0, 0));
                playerEntity.resetJumps();
                resetGlobalAlert();
            }
        }
    }

    public java.util.List<deathBarrier> getDeathZones() {
        return deathZones;
    }

    public void bustedByCamera() {
        playerEntity.loseLife();
        if (playerEntity.getLives() <= 0) {
            loseGame();
            return;
        }
        playerEntity.setPosition(new Vec2(-9f, -6f));
        playerEntity.setLinearVelocity(new Vec2(0, 0));
        resetGlobalAlert();
    }

    public void setView(gamePanel view) {
        this.view = view;
    }

    public player getPlayer() {
        return playerEntity;
    }

    private boolean movingLeft = false;
    private boolean movingRight = false;

    public void setMoveLeft(boolean v) {
        movingLeft = v;
        updateMovement();
    }

    public void setMoveRight(boolean v) {
        movingRight = v;
        updateMovement();
    }

    private void updateMovement() {
        if (state != GameState.PLAYING) {
            playerEntity.stopWalking();
            return;
        }
        if (movingLeft && !movingRight) playerEntity.startWalking(-5);
        else if (movingRight && !movingLeft) playerEntity.startWalking(5);
        else playerEntity.stopWalking();
    }

    public void tryCollectLoot() {
        if (lootInRange != null) {
            playerEntity.collectLoot(1);
            lootInRange.destroy();
            lootItems.remove(lootInRange);
            lootInRange = null;
        }
    }

    public void requestJump() {
        if (state == GameState.PLAYING) playerEntity.doJump();
    }

    public org.jbox2d.common.Vec2 getPlayerPosition() {
        return playerEntity.getPosition();
    }

    public java.util.List<StaticBody> getPlatforms() {
        return platforms;
    }

    public java.util.List<cameraSensor> getCameras() {
        return cameras;
    }

    public int getGlobalAlertLevel() {
        return globalAlertLevel;
    }

    public void resetGlobalAlert() {
        globalAlertLevel = 0;
    }

    public void updateViewCamera() {
        if (view != null) {
            view.updateCamera();
        }
    }

    public void toggleDoubleJump() {
        playerEntity.toggleDoubleJump();
    }

    public GameState getState() {
        return state;
    }

    public void startGame() {
        if (state == GameState.TITLE) {
            state = GameState.PLAYING;
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
        }
    }

    public long getElapsedTime() {
        if (state == GameState.PLAYING) {
            return System.currentTimeMillis() - startTime;
        }
        return elapsedTime;
    }

    public void winGame() {
        if (state == GameState.PLAYING) {
            elapsedTime = System.currentTimeMillis() - startTime;
            state = GameState.WIN;
        }
    }

    public void loseGame() {
        if (state == GameState.PLAYING) {
            elapsedTime = System.currentTimeMillis() - startTime;
            state = GameState.LOSE;
        }
    }


}
