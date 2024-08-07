package src.tankProgram.game;


import src.tankProgram.GameConstants;
import src.tankProgram.Launcher;
import src.tankProgram.menus.EndGamePanel;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import java.util.List;
import java.util.ArrayList;



/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;

    private Tank t1;
    private Tank t2;

    private final Launcher lf;
    private long tick = 0;

    private BufferedImage background;

    private List<Wall> walls;
    private BufferedImage wallImg;
    private BufferedImage breakableWallImg;

    private BufferedImage bulletImg;
    private BufferedImage heartImg;

    private BufferedImage doubleDamageImg;
    private BufferedImage healthBoostImg;
    private BufferedImage halfHealthImg;

    private Clip powerDownPickupClip;
    private Clip powerUpPickupClip;
    private Clip tankDeathClip;
    private Clip tankLifeDownClip;

    private Clip backgroundMusicClip;
    private Clip bulletHitTankClip;

    private BufferedImage[] animationImages;

    private BufferedImage[] explosionImages;
    private BufferedImage[] powerUpImages;
    private BufferedImage[] powerDownImages;

    private int currentAnimationFrame = 0;
    private long lastAnimationTime = 0;
    private boolean isAnimating = false;
    private int animationX = 0;
    private int animationY = 0;


    /**
     *
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.tick++;
                this.t1.update(this.t2); // update tank
                this.t2.update(this.t1); // update tank
                this.update();


//                for (ExplosionAnimation explosion : activeExplosionsT1) {
//                    explosion.update();
//                }
//                for (ExplosionAnimation explosion : activeExplosionsT2) {
//                    explosion.update();
//                }
                this.repaint();   // redraw game
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our 
                 * loop run at a fixed rate per/sec. 
                */
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        isAnimating = false;

        this.t1.setX(300);
        this.t1.setY(300);
        this.t1.setHealth(100);
        this.t1.setLives(3);
        this.t1.setAngle((short) 0);
        this.t1.getBullets().clear();
        this.t1.setDoubleDamage(false);
        this.t1.setHealthBoost(false);
        this.t1.setHalfHealth(false);

        this.t2.setX(1650);
        this.t2.setY(1650);
        this.t2.setHealth(100);
        this.t2.setLives(3);
        this.t2.setAngle((short) 0);
        this.t2.getBullets().clear();
        this.t2.setDoubleDamage(false);
        this.t2.setHealthBoost(false);
        this.t2.setHalfHealth(false);

        for (Wall wall : walls) {
            wall.setDestroyed(false);
        }


    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        BufferedImage t1img = null;
        BufferedImage t2Img = null;

        int numFrames = 6; // Adjust based on the number of frames you have
        explosionImages = new BufferedImage[numFrames];
        powerUpImages = new BufferedImage[numFrames];
        animationImages = new BufferedImage[numFrames];
        powerDownImages = new BufferedImage[numFrames];

        try {
            /*
             * note class loaders read files from the out folder (build folder in Netbeans) and not the
             * current working directory. When running a jar, class loaders will read from within the jar.
             */
            t1img = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/tank1.png"),
                    "Could not find tank1.png")
            );
            t2Img = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/tank2.png"),
                    "Could not find tank2.png")
            );

            background = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/Background.bmp"),
                    "Could not find Background.bmp")
            );

            wallImg = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/stone_wall01.jpg"),
                    "Could not find Wall1.png")
            );

            breakableWallImg = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/wall2.png"),
                    "Could not find Wall2.png")
            );

            bulletImg = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/Shell.gif"),
                    "Could not find Shell.gif"));
            bulletImg = resizeImage(bulletImg, 20, 20);

            heartImg = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/heart.png"),
                    "Could not find heart.png"));
            heartImg = resizeImage(heartImg, GameConstants.LIVES_ICON_SIZE, GameConstants.LIVES_ICON_SIZE);

            doubleDamageImg = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/Pickup.gif"),
                    "Could not find Pickup.gif"));
            doubleDamageImg = resizeImage(doubleDamageImg, 50, 50);

            healthBoostImg = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/2xHealth.png"),
                    "Could not find 2xHealth.png"));
            healthBoostImg = resizeImage(healthBoostImg, 50, 50);

            halfHealthImg = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/halfHealthHeart.png"),
                    "Could not find halfHealthHeart.png"));
            halfHealthImg = resizeImage(halfHealthImg, 50, 50);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("resources/powerDown.wav"),
                            "Could not find powerDown.wav"));
            powerDownPickupClip = AudioSystem.getClip();
            powerDownPickupClip.open(audioInputStream);

            AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("resources/PowerUp.wav"),
                            "Could not find powerUp.wav"));
            powerUpPickupClip = AudioSystem.getClip();
            powerUpPickupClip.open(audioInputStream2);

            AudioInputStream audioInputStream3 = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("resources/TankDeath.wav"),
                            "Could not find Explosion_large.wav"));
            tankDeathClip = AudioSystem.getClip();
            tankDeathClip.open(audioInputStream3);

            AudioInputStream audioInputStream4 = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("resources/TankLifeDown.wav"),
                            "Could not find Explosion_large.wav"));
            tankLifeDownClip = AudioSystem.getClip();
            tankLifeDownClip.open(audioInputStream4);

            AudioInputStream backgroundMusicStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("resources/Music.mid"),
                            "Could not find Music.wav"));
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(backgroundMusicStream);

            AudioInputStream bulletHitTankStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("resources/Explosion_small.wav"),
                            "Could not find Explosion_small.wav"));
            bulletHitTankClip = AudioSystem.getClip();
            bulletHitTankClip.open(bulletHitTankStream);

            explosionImages[0] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_lg/explosion_lg_0001.png")));
            explosionImages[1] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_lg/explosion_lg_0002.png")));
            explosionImages[2] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_lg/explosion_lg_0003.png")));
            explosionImages[3] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_lg/explosion_lg_0004.png")));
            explosionImages[4] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_lg/explosion_lg_0005.png")));
            explosionImages[5] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_lg/explosion_lg_0007.png")));

            powerDownImages[0] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_sm/explosion_sm_0001.png")));
            powerDownImages[1] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_sm/explosion_sm_0002.png")));
            powerDownImages[2] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_sm/explosion_sm_0003.png")));
            powerDownImages[3] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_sm/explosion_sm_0004.png")));
            powerDownImages[4] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_sm/explosion_sm_0005.png")));
            powerDownImages[5] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/explosion_sm/explosion_sm_0006.png")));

            powerUpImages[0] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/BlueFlame/blue_flame_1.png")));
            powerUpImages[1] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/BlueFlame/blue_flame_2.png")));
            powerUpImages[2] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/BlueFlame/blue_flame_3.png")));
            powerUpImages[3] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/BlueFlame/blue_flame_4.png")));
            powerUpImages[4] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/BlueFlame/blue_flame_5.png")));
            powerUpImages[5] = ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("resources/BlueFlame/blue_flame_6.png")));





        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        t1 = new Tank(300, 300, 0, 0, (short) 0, t1img, bulletImg);
        t1.setR(2); // Set the speed of t1
        t1.setROTATIONSPEED(1.0f); // Set the rotation speed of t1
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        this.lf.getJf().addKeyListener(tc1);

        t2 = new Tank(1650, 1650, 0, 0, (short) 0, t2Img, bulletImg);
        t2.setR(2); // Set the speed of t2 to match t1
        t2.setROTATIONSPEED(1.0f); // Set the rotation speed of t2 to match t1
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_N);
        this.lf.getJf().addKeyListener(tc2);

        generateWalls();
        playBackgroundMusic();

    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    public void startAnimation(int x, int y, BufferedImage[] imgArray) {
        this.animationX = x;
        this.animationY = y;
        this.currentAnimationFrame = 0;
        this.isAnimating = true;
        this.lastAnimationTime = System.currentTimeMillis();
        this.animationImages = imgArray;

    }

    public void drawAnimation(Graphics g, BufferedImage[] animationImages) {
        if (isAnimating) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAnimationTime > 100) { // Change frame every 100ms
                currentAnimationFrame++;
                lastAnimationTime = currentTime;
            }

            if (currentAnimationFrame < animationImages.length) {
                g.drawImage(animationImages[currentAnimationFrame], animationX, animationY, null);
            } else {
                isAnimating = false; // End animation
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        // Draw the background
        for (int x = 0; x < GameConstants.GAME_WORLD_WIDTH; x += background.getWidth()) {
            for (int y = 0; y < GameConstants.GAME_WORLD_HEIGHT; y += background.getHeight()) {
                buffer.drawImage(background, x, y, null);
            }
        }

        //draw the walls
        // Draw walls
        for (Wall wall : walls) {
            if (!wall.isDestroyed()) {
                wall.drawImage(buffer);
            } else if (wall.isPowerUpVisible()) {
                buffer.drawImage(wall.getPowerUpImg(), (int) wall.getX(), (int) wall.getY(), null);
            }
        }

        // Draw the tank
        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);
        g2.drawImage(world, 0, 0, null);

        for (Bullet bullet : t1.getBullets()) {
            if (!bullet.isDestroyed()) {
                bullet.drawImage(buffer);
            }
        }

        for (Bullet bullet : t2.getBullets()) {
            if (!bullet.isDestroyed()) {
                bullet.drawImage(buffer);
            }
        }

        // Calculate the viewports for each tank
        int splitScreenWidth = GameConstants.GAME_SCREEN_WIDTH / 2;
        int splitScreenHeight = GameConstants.GAME_SCREEN_HEIGHT;

        // Calculate the camera positions
        int t1CameraX = Math.max(0, Math.min((int) t1.getX() - splitScreenWidth / 2, GameConstants.GAME_WORLD_WIDTH - splitScreenWidth));
        int t1CameraY = Math.max(0, Math.min((int) t1.getY() - splitScreenHeight / 2, GameConstants.GAME_WORLD_HEIGHT - splitScreenHeight));

        int t2CameraX = Math.max(0, Math.min((int) t2.getX() - splitScreenWidth / 2, GameConstants.GAME_WORLD_WIDTH - splitScreenWidth));
        int t2CameraY = Math.max(0, Math.min((int) t2.getY() - splitScreenHeight / 2, GameConstants.GAME_WORLD_HEIGHT - splitScreenHeight));

//        for (ExplosionAnimation explosion : activeExplosionsT1) {
//            explosion.render(g2, explosion.getX(), explosion.getY()); // Provide x and y coordinates for each explosion
//        }
//        for (ExplosionAnimation explosion : activeExplosionsT2) {
//            explosion.render(g2, explosion.getX(), explosion.getY()); // Provide x and y coordinates for each explosion
//        }


        // Draw the left half (for t1)
        g2.drawImage(world.getSubimage(t1CameraX, t1CameraY, splitScreenWidth, splitScreenHeight), 0, 0, null);

        // Draw the right half (for t2)
        g2.drawImage(world.getSubimage(t2CameraX, t2CameraY, splitScreenWidth, splitScreenHeight), splitScreenWidth, 0, null);

        //draw a divider between the screens
        g2.setColor(Color.BLACK);
        g2.fillRect(splitScreenWidth - 2, 0, 4, GameConstants.GAME_SCREEN_HEIGHT);

        // Draw UI panel background
        g2.setColor(new Color(50, 50, 50, 200)); // Semi-transparent background
        g2.fillRect(0, 0, GameConstants.GAME_SCREEN_WIDTH, GameConstants.UI_PANEL_HEIGHT);

        // Draw Player 1's health bar and lives
//        drawPlayerInfo(g2, t1, 50, 10);
//
//        // Draw Player 2's health bar and lives
//        drawPlayerInfo(g2, t2, GameConstants.GAME_SCREEN_WIDTH - 300, 10);

        // Draw Player 1's health bar and lives
        drawPlayerInfo(g2, t1, 40, 10, "Player 1", true);

        // Draw Player 2's health bar and lives
        drawPlayerInfo(g2, t2, GameConstants.GAME_SCREEN_WIDTH - 40, 10, "Player 2", false);

        // Draw the mini-map
        drawMiniMap(g2);

        //draw explosion at x,y
        drawAnimation(g, animationImages);


//        for (ExplosionAnimation explosion : activeExplosionsT1) {
//            explosion.render(g2, (int) t1.getX(), (int) t1.getY()); // Provide x and y coordinates for each explosion
//        }
//
//        for (ExplosionAnimation explosion : activeExplosionsT2) {
//            explosion.render(g2, (int) t2.getX(), (int) t2.getY()); // Provide x and y coordinates for each explosion
//        }
    }

//    private void drawPlayerInfo(Graphics2D g, Tank tank, int x, int y) {
//        // Draw health bar background
//        g.setColor(Color.RED);
//        g.fillRect(x, y, GameConstants.HEALTH_BAR_WIDTH, GameConstants.HEALTH_BAR_HEIGHT);
//
//        // Draw current health
//        g.setColor(Color.GREEN);
//        int healthWidth = (int) (tank.getHealth() / 100.0 * GameConstants.HEALTH_BAR_WIDTH);
//        g.fillRect(x, y, healthWidth, GameConstants.HEALTH_BAR_HEIGHT);
//
//        // Draw health bar border
//        g.setColor(Color.BLACK);
//        g.drawRect(x, y, GameConstants.HEALTH_BAR_WIDTH, GameConstants.HEALTH_BAR_HEIGHT);
//
//        // Draw lives (heart icons)
//        for (int i = 0; i < tank.getLives(); i++) {
//            int heartX = x + GameConstants.HEALTH_BAR_WIDTH + 10 + i * (GameConstants.LIVES_ICON_SIZE + 5);
//            int heartY = y;
//            g.drawImage(heartImg, heartX, heartY, GameConstants.LIVES_ICON_SIZE, GameConstants.LIVES_ICON_SIZE, null);
//        }
//
//        // Optionally, draw the number of lives left
////        g.setColor(Color.WHITE);
////        g.drawString("Lives: " + tank.getLives(), x + GameConstants.HEALTH_BAR_WIDTH + 60, y + 15);
//    }

    private void playBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
    }


    private void drawPlayerInfo(Graphics2D g, Tank tank, int x, int y, String playerLabel, boolean isPlayer1) {
        // Draw lives (heart icons)
        for (int i = 0; i < tank.getLives(); i++) {
            int heartX = isPlayer1 ? x + i * (GameConstants.LIVES_ICON_SIZE + 5) : x - i * (GameConstants.LIVES_ICON_SIZE + 5);
            int heartY = y;
            g.drawImage(heartImg, heartX, heartY, GameConstants.LIVES_ICON_SIZE, GameConstants.LIVES_ICON_SIZE, null);
        }

        // Draw health bar background
        int healthBarX = isPlayer1 ? x + tank.getLives() * (GameConstants.LIVES_ICON_SIZE + 5) + 10 : x - tank.getLives() * (GameConstants.LIVES_ICON_SIZE + 5) - GameConstants.HEALTH_BAR_WIDTH - 10;
        g.setColor(Color.RED);
        g.fillRect(healthBarX, y, GameConstants.HEALTH_BAR_WIDTH, GameConstants.HEALTH_BAR_HEIGHT);

        // Draw current health
        g.setColor(Color.GREEN);
        int healthWidth = (int) (tank.getHealth() / 100.0 * GameConstants.HEALTH_BAR_WIDTH);
        g.fillRect(healthBarX, y, healthWidth, GameConstants.HEALTH_BAR_HEIGHT);

        // Draw health bar border
        g.setColor(Color.BLACK);
        g.drawRect(healthBarX, y, GameConstants.HEALTH_BAR_WIDTH, GameConstants.HEALTH_BAR_HEIGHT);

        // Draw player label
        int labelX = isPlayer1 ? healthBarX + GameConstants.HEALTH_BAR_WIDTH + GameConstants.HEALTH_BAR_WIDTH : healthBarX - GameConstants.HEALTH_BAR_WIDTH - GameConstants.HEALTH_BAR_WIDTH + 90;
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString(playerLabel, labelX, y + GameConstants.HEALTH_BAR_HEIGHT);
    }


    private void drawMiniMap(Graphics2D g) {
        int miniMapX = (GameConstants.GAME_SCREEN_WIDTH / 2) - (GameConstants.MINI_MAP_WIDTH / 2);
        int miniMapY = 10; // Just an example, adjust as necessary

        // Calculate scale factors for the mini-map
        float scaleX = (float) GameConstants.MINI_MAP_WIDTH / GameConstants.GAME_WORLD_WIDTH;
        float scaleY = (float) GameConstants.MINI_MAP_HEIGHT / GameConstants.GAME_WORLD_HEIGHT;

        // Create a mini-world image
        BufferedImage miniWorld = new BufferedImage(GameConstants.MINI_MAP_WIDTH, GameConstants.MINI_MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D miniBuffer = miniWorld.createGraphics();

        // Scale down the game world
        miniBuffer.scale(scaleX, scaleY);
        miniBuffer.drawImage(world, 0, 0, null);

        // Draw the mini-world onto the screen
        g.drawImage(miniWorld, miniMapX, miniMapY, null);

        // Draw border for mini-map
        g.setColor(Color.BLACK);
        g.drawRect(miniMapX, miniMapY, GameConstants.MINI_MAP_WIDTH, GameConstants.MINI_MAP_HEIGHT);
    }


    // Collision handling method
    public void handleTankWallCollision(Tank tank, Wall wall) {
        // Get the bounding boxes of the tank and wall
        Rectangle tankRect = new Rectangle((int) tank.getX(), (int) tank.getY(), tank.getImg().getWidth(), tank.getImg().getHeight());
        Rectangle wallRect = new Rectangle((int) wall.getX(), (int) wall.getY(), (int) wall.getWidth(), (int) wall.getHeight());

        // Check for collision
        if (tankRect.intersects(wallRect)) {
            // Determine which side of the wall the tank is colliding with
            float overlapX = 0;
            float overlapY = 0;

            // Calculate overlap on the x-axis
            if (tank.getX() + tank.getImg().getWidth() > wall.getX() && tank.getX() < wall.getX() + wall.getWidth()) {
                if (tank.getX() < wall.getX()) { // Left collision
                    overlapX = (tank.getX() + tank.getImg().getWidth()) - wall.getX();
                } else { // Right collision
                    overlapX = tank.getX() - (wall.getX() + wall.getWidth());
                }
            }

            // Calculate overlap on the y-axis
            if (tank.getY() + tank.getImg().getHeight() > wall.getY() && tank.getY() < wall.getY() + wall.getHeight()) {
                if (tank.getY() < wall.getY()) { // Top collision
                    overlapY = (tank.getY() + tank.getImg().getHeight()) - wall.getY();
                } else { // Bottom collision
                    overlapY = tank.getY() - (wall.getY() + wall.getHeight());
                }
            }

            // Determine which axis to resolve collision on (the smaller overlap)
            if (Math.abs(overlapX) < Math.abs(overlapY)) {
                // Horizontal collision
                if (overlapX > 0) { // Left side collision
                    tank.setX(wall.getX() - tank.getImg().getWidth());
                } else { // Right side collision
                    tank.setX(wall.getX() + wall.getWidth());
                }
                tank.setVx(0); // Stop horizontal movement
            } else {
                // Vertical collision
                if (overlapY > 0) { // Top collision
                    tank.setY(wall.getY() - tank.getImg().getHeight());
                } else { // Bottom collision
                    tank.setY(wall.getY() + wall.getHeight());
                }
                tank.setVy(0); // Stop vertical movement
            }
        }
    }



    public void handleTankCollision(Tank tank1, Tank tank2) {
        Rectangle tank1Rect = new Rectangle((int) tank1.getX(), (int) tank1.getY(), tank1.getImg().getWidth(), tank1.getImg().getHeight());
        Rectangle tank2Rect = new Rectangle((int) tank2.getX(), (int) tank2.getY(), tank2.getImg().getWidth(), tank2.getImg().getHeight());

        if (tank1Rect.intersects(tank2Rect)) {
            // Calculate overlap distances
            float overlapX = 0;
            float overlapY = 0;

            if (tank1.getX() < tank2.getX()) {
                overlapX = (tank1.getX() + tank1.getImg().getWidth()) - tank2.getX();
            } else {
                overlapX = tank1.getX() - (tank2.getX() + tank2.getImg().getWidth());
            }

            if (tank1.getY() < tank2.getY()) {
                overlapY = (tank1.getY() + tank1.getImg().getHeight()) - tank2.getY();
            } else {
                overlapY = tank1.getY() - (tank2.getY() + tank2.getImg().getHeight());
            }

            // Determine which tank is moving
            boolean tank1Moving = Math.abs(tank1.getVx()) > 0 || Math.abs(tank1.getVy()) > 0;
            boolean tank2Moving = Math.abs(tank2.getVx()) > 0 || Math.abs(tank2.getVy()) > 0;

            // Prevent tanks from passing through each other
            if (Math.abs(overlapX) < Math.abs(overlapY)) {
                // Horizontal collision
                if (tank1Moving) {
                    if (overlapX > 0) {
                        tank1.setX(tank1.getX() - Math.abs(overlapX));
                    } else {
                        tank1.setX(tank1.getX() + Math.abs(overlapX));
                    }
                    tank1.setVx(0);
                }
                if (tank2Moving) {
                    if (overlapX > 0) {
                        tank2.setX(tank2.getX() + Math.abs(overlapX));
                    } else {
                        tank2.setX(tank2.getX() - Math.abs(overlapX));
                    }
                    tank2.setVx(0);
                }
            } else {
                // Vertical collision
                if (tank1Moving) {
                    if (overlapY > 0) {
                        tank1.setY(tank1.getY() - Math.abs(overlapY));
                    } else {
                        tank1.setY(tank1.getY() + Math.abs(overlapY));
                    }
                    tank1.setVy(0);
                }
                if (tank2Moving) {
                    if (overlapY > 0) {
                        tank2.setY(tank2.getY() + Math.abs(overlapY));
                    } else {
                        tank2.setY(tank2.getY() - Math.abs(overlapY));
                    }
                    tank2.setVy(0);
                }
            }
        }
    }


    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start(); // Start playing
        }
    }


    private void checkPowerUpCollisions(Tank tank) {
        for (Wall wall : walls) {
            if (wall.isDestroyed() && wall.isPowerUpVisible()) {
                Rectangle tankBounds = tank.getBounds();
                Rectangle powerUpBounds = new Rectangle((int) wall.getX(), (int) wall.getY(), (int) wall.getPowerUpImg().getWidth(), (int) wall.getPowerUpImg().getHeight());

                if (tankBounds.intersects(powerUpBounds)) {
                    if (wall.isDoubleDamage() && !tank.isDoubleDamage()) {
                        tank.setDoubleDamage(true);
//                        wall.setDoubleDamage(false);
                        playSound(powerUpPickupClip);
                        wall.setPowerUpVisible(false); // Remove power-up from map
                        startAnimation(400, 500, powerUpImages);
                    } else if (wall.isHealthBoost() && !tank.isHealthBoost()) {
                        tank.setHealthBoost(true);
                        tank.setHealth(200); // Set health to 200
//                        wall.setHealthBoost(false);
                        playSound(powerUpPickupClip);
                        wall.setPowerUpVisible(false); // Remove power-up from map
                        startAnimation(400, 500, powerUpImages);
                    } else if (wall.isHalfHealth() && !tank.isHalfHealth()) {
                        tank.setHalfHealth(true);
                        tank.setHealth(tank.getHealth() / 2); // Halve the current health
//                        wall.setHalfHealth(false);
                        playSound(powerDownPickupClip);
                        wall.setPowerUpVisible(false); // Remove power-up from map
                        startAnimation(400, 500, powerDownImages);
                    }


                    // Additional power-up checks can be added here in the future
                }
            }
        }
    }


    public void handleBulletCollisions() {
        List<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : t1.getBullets()) {
            if (!bullet.isDestroyed()) {
                for (Wall wall : walls) {
                    if (!wall.isDestroyed() && bullet.getBounds().intersects(wall.getBounds())) {
                        bulletsToRemove.add(bullet);
                        if (wall.isDestructible()) {
                            wall.setDestroyed(true);
                            if (wall.isDoubleDamage()){
                                //write code for draw powerup img on gameboard
                                // if collision detected set isDoubleDamage for tank object to be true with the checkPowerUpCollisions method
//                                   once the collision occurs remove the drawn image of the powerup
                                wall.setPowerUpImg(doubleDamageImg); // Set the power-up image
                                wall.setPowerUpVisible(true); // Make the power-up visible
                            }
                            else if (wall.isHealthBoost()) {
                                wall.setPowerUpImg(healthBoostImg);
                                wall.setPowerUpVisible(true);
                            }
                            else if (wall.isHalfHealth()) {
                                wall.setPowerUpImg(halfHealthImg);
                                wall.setPowerUpVisible(true);
                            }
//                            // more else if checks for other powerups
                        }
                        break;
                    }
                }
                if (bullet.getBounds().intersects(t2.getBounds())) {
                    bulletsToRemove.add(bullet);
                    playSound(bulletHitTankClip);

                    if (t1.isDoubleDamage()){
                        t2.reduceHealth(40); // Reduce health by 40 for powerup
                    }else {
                        t2.reduceHealth(20); // Reduce health by 20
                    }



                    if (t2.getHealth() <= 0) {

                        int lifeCount = t2.getLives() - 1;
                        t2.setLives(lifeCount);

                        if (t2.getLives() < 1) {
                            playSound(tankDeathClip);
                        } else {
                            playSound(tankLifeDownClip);
                        }
//                        activeExplosions.add(new ExplosionAnimation(explosionImages, 100));

                        if (t2.getLives() <= 0){
                            showEndGameScreen("Player 1 Wins!");
//                            lf.setFrame("end"); // Trigger end game
//                            resetGame();
//                            System.out.println("Player 1 wins!");
                        }

                        this.t2.setX(1650);
                        this.t2.setY(1650);
                        this.t2.setHealth(100);
                        this.t2.setAngle((short) 0);
                        this.t2.setDoubleDamage(false);
                        startAnimation(1125, 625, explosionImages); // animation for death



                    }
                }
            }
        }
        t1.getBullets().removeAll(bulletsToRemove);

        for (Bullet bullet : t2.getBullets()) {
            if (!bullet.isDestroyed()) {
                for (Wall wall : walls) {
                    if (!wall.isDestroyed() && bullet.getBounds().intersects(wall.getBounds())) {
                        bulletsToRemove.add(bullet);
                        if (wall.isDestructible()) {
                            wall.setDestroyed(true);
                            if (wall.isDoubleDamage()){
                                wall.setPowerUpImg(doubleDamageImg); // Set the power-up image
                                wall.setPowerUpVisible(true); // Make the power-up visible
                            }
                            else if (wall.isHealthBoost()) {
                                wall.setPowerUpImg(healthBoostImg);
                                wall.setPowerUpVisible(true);
                            }
                            else if (wall.isHalfHealth()) {
                                wall.setPowerUpImg(halfHealthImg);
                                wall.setPowerUpVisible(true);
                            }
//                            // more else if checks for other powerups
                        }
                        break;
                    }
                }
                if (bullet.getBounds().intersects(t1.getBounds())) {
                    bulletsToRemove.add(bullet);
                    playSound(bulletHitTankClip);

                    if (t2.isDoubleDamage()) {
                        t1.reduceHealth(40); // Reduce health by 40
                    } else{
                        t1.reduceHealth(20); // Reduce health by 20
                    }



                    if (t1.getHealth() <= 0) {

                        int lifeCount = t1.getLives() - 1;
                        t1.setLives(lifeCount);

                        if (t1.getLives() < 1) {
                            playSound(tankDeathClip);
                        } else {
                            playSound(tankLifeDownClip);
                        }


                        if (t1.getLives() <= 0) {
                            showEndGameScreen("Player 2 Wins!");
//                            lf.setFrame("end"); // Trigger end game
//                            resetGame();
//                            System.out.println("Player 2 wins!");
                        }

                        this.t1.setX(300);
                        this.t1.setY(300);
                        this.t1.setHealth(100);
                        this.t1.setAngle((short) 0);
                        this.t1.setDoubleDamage(false);
                        startAnimation(300, 300, explosionImages); // animation for death

                    }
                }
            }
        }
        t2.getBullets().removeAll(bulletsToRemove);
        bulletsToRemove.clear();

    }

    private void showEndGameScreen(String winnerMessage) {
        // Update end game panel with winner message
        EndGamePanel endGamePanel = (EndGamePanel) lf.getMainPanel().getComponent(2); // Assuming end game panel is at index 2
        endGamePanel.setWinnerMessage(winnerMessage);

        // Switch to the end game screen
        lf.setFrame("end");
        resetGame();
    }


    public void update() {
        t1.update(t2); // Update tank position
        t2.update(t1); // Update tank position

        // Check collisions with walls
        for (Wall wall : walls) {
            if (!wall.isDestroyed()) {
                if (t1.checkCollision(wall)) {
                    handleTankWallCollision(t1, wall);
                }
                if (t2.checkCollision(wall)) {
                    handleTankWallCollision(t2, wall);
                }
            }
        }
        handleBulletCollisions();
        handleTankCollision(t1, t2);

        checkPowerUpCollisions(t1);
        checkPowerUpCollisions(t2);

    }

//    private void generateWalls() {
//        walls = new ArrayList<>();
//
//        // Define wall dimensions
//        int wallWidth = 50;
//        int wallHeight = 50;
//
//        // Unbreakable Border Walls
//        for (int x = 0; x <= GameConstants.GAME_WORLD_WIDTH - wallWidth; x += wallWidth) {
//            walls.add(new Wall(x, 0, wallWidth, wallHeight, false, wallImg)); // Top border
//            walls.add(new Wall(x, GameConstants.GAME_WORLD_HEIGHT - wallHeight, wallWidth, wallHeight, false, wallImg)); // Bottom border
//        }
//        for (int y = 0; y <= GameConstants.GAME_WORLD_HEIGHT - wallHeight; y += wallHeight) {
//            walls.add(new Wall(0, y, wallWidth, wallHeight, false, wallImg)); // Left border
//            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - wallWidth, y, wallWidth, wallHeight, false, wallImg)); // Right border
//        }
//
//        // Central Horizontal Line (Breakable Walls)
//        int centerY = GameConstants.GAME_WORLD_HEIGHT / 2;
//        for (int x = wallWidth * 2; x <= GameConstants.GAME_WORLD_WIDTH - wallWidth * 2; x += wallWidth) {
////            walls.add(new Wall(x, centerY - 50, wallWidth, wallHeight, true, breakableWallImg));
//            walls.add(new Wall(x, centerY, wallWidth, wallHeight, true, breakableWallImg));
//            walls.add(new Wall(x, centerY  + 50, wallWidth, wallHeight, true, breakableWallImg));
//        }
//
//        // Central Vertical Line (Breakable Walls)
//        int centerX = GameConstants.GAME_WORLD_WIDTH / 2;
//        for (int y = 0; y < GameConstants.GAME_WORLD_HEIGHT; y += wallHeight) {
////            walls.add(new Wall(centerX - 50, centerY, wallWidth, wallHeight, true, breakableWallImg));
//            walls.add(new Wall(centerX, y, wallWidth, wallHeight, true, breakableWallImg));
////            walls.add(new Wall(centerX + 50, centerY, wallWidth, wallHeight, true, breakableWallImg));
//        }
//
//        // Symmetric Square and Rectangular Structures (Unbreakable)
//        int[][] symmetricalCoords = {
//                // Top-left
//                {100, 100}, {150, 100}, {200, 100}, {250, 100}, {300, 100}, {350, 100},
//                {100, 150}, {100, 200}, {100, 250}, {100, 300}, {100, 350},
//                {150, 350}, {200, 350}, {250, 350}, {300, 350}, {350, 350},
//                // Top-right
//                {GameConstants.GAME_WORLD_WIDTH - 100, 100}, {GameConstants.GAME_WORLD_WIDTH - 150, 100},
//                {GameConstants.GAME_WORLD_WIDTH - 200, 100}, {GameConstants.GAME_WORLD_WIDTH - 250, 100},
//                {GameConstants.GAME_WORLD_WIDTH - 300, 100}, {GameConstants.GAME_WORLD_WIDTH - 350, 100},
//                {GameConstants.GAME_WORLD_WIDTH - 100, 150}, {GameConstants.GAME_WORLD_WIDTH - 100, 200},
//                {GameConstants.GAME_WORLD_WIDTH - 100, 250}, {GameConstants.GAME_WORLD_WIDTH - 100, 300},
//                {GameConstants.GAME_WORLD_WIDTH - 100, 350}, {GameConstants.GAME_WORLD_WIDTH - 150, 350},
//                {GameConstants.GAME_WORLD_WIDTH - 200, 350}, {GameConstants.GAME_WORLD_WIDTH - 250, 350},
//                {GameConstants.GAME_WORLD_WIDTH - 300, 350}, {GameConstants.GAME_WORLD_WIDTH - 350, 350},
//        };
//        for (int[] coord : symmetricalCoords) {
//            walls.add(new Wall(coord[0], coord[1], wallWidth, wallHeight, false, wallImg));
//        }
//
//        // Additional Maze-like Structures (Unbreakable)
//        int[][] additionalMazeCoords = {
//                {600, 600}, {650, 600}, {700, 600}, {750, 600}, {800, 600},
//                {600, 650}, {600, 700}, {600, 750}, {600, 800},
//                {650, 800}, {700, 800}, {750, 800}, {800, 800},
//                {800, 750}, {800, 700}, {800, 650},
//                // Symmetrical right side
//                {GameConstants.GAME_WORLD_WIDTH - 600, 600}, {GameConstants.GAME_WORLD_WIDTH - 650, 600},
//                {GameConstants.GAME_WORLD_WIDTH - 700, 600}, {GameConstants.GAME_WORLD_WIDTH - 750, 600},
//                {GameConstants.GAME_WORLD_WIDTH - 800, 600}, {GameConstants.GAME_WORLD_WIDTH - 600, 650},
//                {GameConstants.GAME_WORLD_WIDTH - 600, 700}, {GameConstants.GAME_WORLD_WIDTH - 600, 750},
//                {GameConstants.GAME_WORLD_WIDTH - 600, 800}, {GameConstants.GAME_WORLD_WIDTH - 650, 800},
//                {GameConstants.GAME_WORLD_WIDTH - 700, 800}, {GameConstants.GAME_WORLD_WIDTH - 750, 800},
//                {GameConstants.GAME_WORLD_WIDTH - 800, 800}, {GameConstants.GAME_WORLD_WIDTH - 800, 750},
//                {GameConstants.GAME_WORLD_WIDTH - 800, 700}, {GameConstants.GAME_WORLD_WIDTH - 800, 650},
//        };
//        for (int[] coord : additionalMazeCoords) {
//            walls.add(new Wall(coord[0], coord[1], wallWidth, wallHeight, false, wallImg));
//        }
//
//        // Symmetric Squares and Rectangles
//        int[][] squareCoords = {
//                {200, 200}, {200, 250}, {250, 200}, {250, 250},
//                {GameConstants.GAME_WORLD_WIDTH - 300, 200}, {GameConstants.GAME_WORLD_WIDTH - 300, 250},
//                {GameConstants.GAME_WORLD_WIDTH - 250, 200}, {GameConstants.GAME_WORLD_WIDTH - 250, 250},
//                {200, GameConstants.GAME_WORLD_HEIGHT - 300}, {200, GameConstants.GAME_WORLD_HEIGHT - 250},
//                {250, GameConstants.GAME_WORLD_HEIGHT - 300}, {250, GameConstants.GAME_WORLD_HEIGHT - 250},
//                {GameConstants.GAME_WORLD_WIDTH - 300, GameConstants.GAME_WORLD_HEIGHT - 300},
//                {GameConstants.GAME_WORLD_WIDTH - 300, GameConstants.GAME_WORLD_HEIGHT - 250},
//                {GameConstants.GAME_WORLD_WIDTH - 250, GameConstants.GAME_WORLD_HEIGHT - 300},
//                {GameConstants.GAME_WORLD_WIDTH - 250, GameConstants.GAME_WORLD_HEIGHT - 250},
//        };
//        for (int[] coord : squareCoords) {
//            walls.add(new Wall(coord[0], coord[1], wallWidth, wallHeight, false, wallImg));
//        }
//
////        Example: Maze-like area (unbreakable walls)
//        int[][] mazeCoords = {
//                {1200, 150}, {1250, 150}, {1300, 150}, {1350, 150},
//                {1200, 200}, {1350, 200},
//                {1200, 250}, {1350, 250},
//                {1200, 300}, {1250, 300}, {1300, 300}, {1350, 300},
//                {1200, 350}, {1350, 350},
//                {1200, 400}, {1350, 400},
//                {1200, 450}, {1250, 450}, {1300, 450}, {1350, 450},
//        };
//        for (int[] coord : mazeCoords) {
//            walls.add(new Wall(coord[0], coord[1], 50, 50, false, wallImg));
//        }
//
//        int[][] newMazeCoords = {
//                {1150, 550}, {1200, 550}, {1250, 550}, {1300, 550}, {1350, 550},
//                {1150, 600}, {1350, 600},
//                {1150, 650}, {1350, 650},
//                {1150, 700}, {1200, 700}, {1250, 700}, {1300, 700}, {1350, 700},
//                {1150, 750}, {1350, 750},
//                {1150, 800}, {1350, 800},
//                {1150, 850}, {1200, 850}, {1250, 850}, {1300, 850}, {1350, 850},
//        };
//        for (int[] coord : newMazeCoords) {
//            walls.add(new Wall(coord[0], coord[1], 50, 50, false, wallImg));
//        }
//
//        int[][] adjustedMazeCoords = {
//                {1200, 150}, {1250, 150}, {1300, 150}, {1350, 150},
//                {1200, 200}, {1350, 200},
//                {1200, 250}, {1350, 250},
//                {1200, 300}, {1250, 300}, {1300, 300}, {1350, 300},
//                {1200, 350}, {1350, 350},
//                {1200, 400}, {1350, 400},
//                {1200, 450}, {1250, 450}, {1300, 450}, {1350, 450},
//        };
//        for (int[] coord : adjustedMazeCoords) {
//            walls.add(new Wall(coord[0], coord[1], 50, 50, false, wallImg));
//        }
//
//        int[][] adjustedNewMazeCoords = {
//                {1150, 550}, {1200, 550}, {1250, 550}, {1300, 550}, {1350, 550},
//                {1150, 600}, {1350, 600},
//                {1150, 650}, {1350, 650},
//                {1150, 700}, {1200, 700}, {1250, 700}, {1300, 700}, {1350, 700},
//                {1150, 750}, {1350, 750},
//                {1150, 800}, {1350, 800},
//                {1150, 850}, {1200, 850}, {1250, 850}, {1300, 850}, {1350, 850},
//        };
//        for (int[] coord : adjustedNewMazeCoords) {
//            walls.add(new Wall(coord[0], coord[1], 50, 50, false, wallImg));
//        }
//
//
//
//
//
//        powerUpWalls(wallWidth, wallHeight);
//    }

    private void generateWalls() {
        walls = new ArrayList<>();

        // Define wall dimensions
        int wallWidth = 50;
        int wallHeight = 50;

        // Unbreakable Border Walls
        for (int x = 0; x <= GameConstants.GAME_WORLD_WIDTH - wallWidth; x += wallWidth) {
            walls.add(new Wall(x, 0, wallWidth, wallHeight, false, wallImg)); // Top border
            walls.add(new Wall(x, GameConstants.GAME_WORLD_HEIGHT - wallHeight, wallWidth, wallHeight, false, wallImg)); // Bottom border
        }
        for (int y = 0; y <= GameConstants.GAME_WORLD_HEIGHT - wallHeight; y += wallHeight) {
            walls.add(new Wall(0, y, wallWidth, wallHeight, false, wallImg)); // Left border
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - wallWidth, y, wallWidth, wallHeight, false, wallImg)); // Right border
        }

        // Central Horizontal Line (Breakable Walls)
        int centerY = GameConstants.GAME_WORLD_HEIGHT / 2;
        for (int x = wallWidth * 2; x <= GameConstants.GAME_WORLD_WIDTH - wallWidth * 2; x += wallWidth) {
            walls.add(new Wall(x, centerY, wallWidth, wallHeight, true, breakableWallImg));
            walls.add(new Wall(x, centerY  + 50, wallWidth, wallHeight, true, breakableWallImg));
        }

        // Central Vertical Line (Breakable Walls)
        int centerX = GameConstants.GAME_WORLD_WIDTH / 2;
        for (int y = 0; y < GameConstants.GAME_WORLD_HEIGHT; y += wallHeight) {
            walls.add(new Wall(centerX, y, wallWidth, wallHeight, true, breakableWallImg));
        }

        // Symmetric Square and Rectangular Structures (Unbreakable) and Mirrored
        int[][] symmetricalCoords = {
                // Top-left
                {100, 100}, {150, 100}, {200, 100}, {250, 100}, {300, 100}, {350, 100},
                {100, 150}, {100, 200}, {100, 250}, {100, 300}, {100, 350},
                {150, 350}, {200, 350}, {250, 350}, {300, 350}, {350, 350},
        };
        for (int[] coord : symmetricalCoords) {
            walls.add(new Wall(coord[0], coord[1], wallWidth, wallHeight, false, wallImg)); // Top-left
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - wallWidth, coord[1], wallWidth, wallHeight, false, wallImg)); // Top-right
            walls.add(new Wall(coord[0], GameConstants.GAME_WORLD_HEIGHT - coord[1] - wallHeight, wallWidth, wallHeight, false, wallImg)); // Bottom-left
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - wallWidth, GameConstants.GAME_WORLD_HEIGHT - coord[1] - wallHeight, wallWidth, wallHeight, false, wallImg)); // Bottom-right
        }

        // Additional Maze-like Structures (Unbreakable) and Mirrored
        int[][] additionalMazeCoords = {
                {600, 600}, {650, 600}, {700, 600}, {750, 600}, {800, 600},
                {600, 650}, {600, 700}, {600, 750}, {600, 800},
                {650, 800}, {700, 800}, {750, 800}, {800, 800},
                {800, 750}, {800, 700}, {800, 650},
        };
        for (int[] coord : additionalMazeCoords) {
            walls.add(new Wall(coord[0], coord[1], wallWidth, wallHeight, false, wallImg)); // Original
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - wallWidth, coord[1], wallWidth, wallHeight, false, wallImg)); // Mirrored horizontally
            walls.add(new Wall(coord[0], GameConstants.GAME_WORLD_HEIGHT - coord[1] - wallHeight, wallWidth, wallHeight, false, wallImg)); // Mirrored vertically
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - wallWidth, GameConstants.GAME_WORLD_HEIGHT - coord[1] - wallHeight, wallWidth, wallHeight, false, wallImg)); // Mirrored both axes
        }

        // Symmetric Squares and Rectangles
        int[][] squareCoords = {
                {200, 200}, {200, 250}, {250, 200}, {250, 250},
                {GameConstants.GAME_WORLD_WIDTH - 300, 200}, {GameConstants.GAME_WORLD_WIDTH - 300, 250},
                {GameConstants.GAME_WORLD_WIDTH - 250, 200}, {GameConstants.GAME_WORLD_WIDTH - 250, 250},
                {200, GameConstants.GAME_WORLD_HEIGHT - 300}, {200, GameConstants.GAME_WORLD_HEIGHT - 250},
                {250, GameConstants.GAME_WORLD_HEIGHT - 300}, {250, GameConstants.GAME_WORLD_HEIGHT - 250},
                {GameConstants.GAME_WORLD_WIDTH - 300, GameConstants.GAME_WORLD_HEIGHT - 300},
                {GameConstants.GAME_WORLD_WIDTH - 300, GameConstants.GAME_WORLD_HEIGHT - 250},
                {GameConstants.GAME_WORLD_WIDTH - 250, GameConstants.GAME_WORLD_HEIGHT - 300},
                {GameConstants.GAME_WORLD_WIDTH - 250, GameConstants.GAME_WORLD_HEIGHT - 250},
        };
        for (int[] coord : squareCoords) {
            walls.add(new Wall(coord[0], coord[1], wallWidth, wallHeight, false, wallImg));
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - wallWidth, coord[1], wallWidth, wallHeight, false, wallImg)); // Mirrored horizontally
            walls.add(new Wall(coord[0], GameConstants.GAME_WORLD_HEIGHT - coord[1] - wallHeight, wallWidth, wallHeight, false, wallImg)); // Mirrored vertically
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - wallWidth, GameConstants.GAME_WORLD_HEIGHT - coord[1] - wallHeight, wallWidth, wallHeight, false, wallImg)); // Mirrored both axes
        }

        // Example: Maze-like area (unbreakable walls)
        int[][] mazeCoords = {
                {1200, 150}, {1250, 150}, {1300, 150}, {1350, 150},
                {1200, 200}, {1350, 200},
                {1200, 250}, {1350, 250},
                {1200, 300}, {1250, 300}, {1300, 300}, {1350, 300},
                {1200, 350}, {1350, 350},
                {1200, 400}, {1350, 400},
                {1200, 450}, {1250, 450}, {1300, 450}, {1350, 450},
        };
        for (int[] coord : mazeCoords) {
            walls.add(new Wall(coord[0], coord[1], 50, 50, false, wallImg));
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, coord[1], 50, 50, false, wallImg)); // Mirrored horizontally
            walls.add(new Wall(coord[0], GameConstants.GAME_WORLD_HEIGHT - coord[1] - 50, 50, 50, false, wallImg)); // Mirrored vertically
//            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, GameConstants.GAME_WORLD_HEIGHT - coord[1] - 50, 50, 50, false, wallImg)); // Mirrored both axes
        }

        int[][] newMazeCoords = {
                {1150, 550}, {1200, 550}, {1250, 550}, {1300, 550}, {1350, 550},
                {1150, 600}, {1350, 600},
                {1150, 650}, {1350, 650},
                {1150, 700}, {1200, 700}, {1250, 700}, {1300, 700}, {1350, 700},
                {1150, 750}, {1350, 750},
                {1150, 800}, {1200, 800}, {1250, 800}, {1300, 800}, {1350, 800},
        };
        for (int[] coord : newMazeCoords) {
            walls.add(new Wall(coord[0], coord[1], 50, 50, false, wallImg));
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, coord[1], 50, 50, false, wallImg)); // Mirrored horizontally
            walls.add(new Wall(coord[0], GameConstants.GAME_WORLD_HEIGHT - coord[1] - 50, 50, 50, false, wallImg)); // Mirrored vertically
//            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, GameConstants.GAME_WORLD_HEIGHT - coord[1] - 50, 50, 50, false, wallImg)); // Mirrored both axes
        }

        // Adding random unbreakable walls (ensure they're not close to powerup walls)
        addRandomUnbreakableWalls();

        powerUpWalls(wallWidth, wallHeight);
    }

    private void addRandomUnbreakableWalls() {
        int[][] randomWalls = {
                {600, 200}, {850, 1000}, {450, 1400}, {800, 1250}, {1600, 1500},
                {1750, 1100}, {1200, 850}, {300, 950}, {400, 500}, {1700, 700}
        };
        for (int[] coord : randomWalls) {
            walls.add(new Wall(coord[0], coord[1], 50, 100, false, wallImg));
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, coord[1], 50, 100, false, wallImg)); // Mirrored horizontally
            walls.add(new Wall(coord[0], GameConstants.GAME_WORLD_HEIGHT - coord[1] - 100, 50, 100, false, wallImg)); // Mirrored vertically
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, GameConstants.GAME_WORLD_HEIGHT - coord[1] - 100, 50, 100, false, wallImg)); // Mirrored both axes
        }

        int[][] utahShapeCoords = {
                {950, 400}, {1000, 400}, {1050, 400}, {950, 450}, {1050, 450}, {950, 500}, {1050, 500}
        };
        for (int[] coord : utahShapeCoords) {
            walls.add(new Wall(coord[0], coord[1], 50, 50, false, wallImg));
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, coord[1], 50, 50, false, wallImg)); // Mirrored horizontally
            walls.add(new Wall(coord[0], GameConstants.GAME_WORLD_HEIGHT - coord[1] - 50, 50, 50, false, wallImg)); // Mirrored vertically
            walls.add(new Wall(GameConstants.GAME_WORLD_WIDTH - coord[0] - 50, GameConstants.GAME_WORLD_HEIGHT - coord[1] - 50, 50, 50, false, wallImg)); // Mirrored both axes
        }
    }


    void powerUpWalls(int wallWidth, int wallHeight) {
        // Six power-ups placed symmetrically
        Wall breakableWall1 = new Wall(GameConstants.GAME_WORLD_WIDTH / 2 - 200, GameConstants.GAME_WORLD_HEIGHT / 2, wallWidth, wallHeight, true, breakableWallImg);
        breakableWall1.setDoubleDamage(true);
        breakableWall1.setPowerUpImg(doubleDamageImg);
        walls.add(breakableWall1);

        Wall breakableWall2 = new Wall(GameConstants.GAME_WORLD_WIDTH / 2 + 200, GameConstants.GAME_WORLD_HEIGHT / 2, wallWidth, wallHeight, true, breakableWallImg);
        breakableWall2.setHealthBoost(true);
        breakableWall2.setPowerUpImg(healthBoostImg);
        walls.add(breakableWall2);

        Wall breakableWall3 = new Wall(GameConstants.GAME_WORLD_WIDTH / 2, GameConstants.GAME_WORLD_HEIGHT / 2 - 200, wallWidth, wallHeight, true, breakableWallImg);
        breakableWall3.setHalfHealth(true);
        breakableWall3.setPowerUpImg(halfHealthImg);
        walls.add(breakableWall3);

//        Wall breakableWall4 = new Wall(GameConstants.GAME_WORLD_WIDTH / 2, GameConstants.GAME_WORLD_HEIGHT / 2 + 200, wallWidth, wallHeight, true, breakableWallImg);
//        breakableWall4.setDoubleDamage(true);
//        breakableWall4.setPowerUpImg(doubleDamageImg);
//        walls.add(breakableWall4);
//
//        Wall breakableWall5 = new Wall(GameConstants.GAME_WORLD_WIDTH / 2 + 400, GameConstants.GAME_WORLD_HEIGHT / 2 + 400, wallWidth, wallHeight, true, breakableWallImg);
//        breakableWall5.setHealthBoost(true);
//        breakableWall5.setPowerUpImg(healthBoostImg);
//        walls.add(breakableWall5);
//
//        Wall breakableWall6 = new Wall(GameConstants.GAME_WORLD_WIDTH / 2 - 400, GameConstants.GAME_WORLD_HEIGHT / 2 - 400, wallWidth, wallHeight, true, breakableWallImg);
//        breakableWall6.setHalfHealth(true);
//        breakableWall6.setPowerUpImg(halfHealthImg);
//        walls.add(breakableWall6);
    }



}
