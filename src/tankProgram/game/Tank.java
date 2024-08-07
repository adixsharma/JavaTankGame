package src.tankProgram.game;

import src.tankProgram.GameConstants;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anthony-pc
 */
public class Tank{

    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private int health;
    private int lives;
    private boolean isDoubleDamage; // first powerup
    private boolean isHealthBoost; // Indicates if health boost Power Up is active
    private boolean isHalfHealth; // Third power-up


    private float R = 5;
    private float ROTATIONSPEED = 3.0f;

    private BufferedImage img;

    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;

    private List<Bullet> bullets;
    private BufferedImage bulletImg; // Image for the bullet


    // Constructor for each tank object sets some default values
    public Tank(float x, float y, float vx, float vy, float angle, BufferedImage img, BufferedImage bulletImg) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.img = img;
        this.bulletImg = bulletImg;
        this.bullets = new ArrayList<>();
        this.health = 100;
        this.lives = 3;
        this.isDoubleDamage = false; // power up
        this.isHealthBoost = false; // power up
        this.isHalfHealth = false; // power up
    }

    // Getters and Setters

    void setX(float x){ this.x = x; }

    void setY(float y) { this. y = y;}

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    public boolean isDoubleDamage() {
        return isDoubleDamage;
    }

    public void setDoubleDamage(boolean doubleDamage) {
        isDoubleDamage = doubleDamage;
    }

    public boolean isHealthBoost() {
        return isHealthBoost;
    }

    public void setHealthBoost(boolean healthBoost) {
        isHealthBoost = healthBoost;
    }

    public boolean isHalfHealth() {
        return isHalfHealth;
    }

    public void setHalfHealth(boolean halfHealth) {
        isHalfHealth = halfHealth;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void reduceHealth(int amount) {
        this.health -= amount;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public BufferedImage getImg() {
        return img;
    }


    public void setVx(float vx) {
        this.vx = vx;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public void setR(float R) {
        this.R = R;
    }

    public void setROTATIONSPEED(float ROTATIONSPEED) {
        this.ROTATIONSPEED = ROTATIONSPEED;
    }

    public float getR() {
        return R;
    }

    public float getROTATIONSPEED() {
        return ROTATIONSPEED;
    }

    public boolean checkCollision(Tank otherTank) {
        Rectangle thisTankRect = this.getBounds();
        Rectangle otherTankRect = otherTank.getBounds();
        return thisTankRect.intersects(otherTankRect);
    }

    public void handleCollision(Tank otherTank) {
        if (this.checkCollision(otherTank)) {
            // Reset the position of this tank to prevent overlap
            this.x -= this.vx;
            this.y -= this.vy;
        }
    }


    // need to center it better
    public void shoot() {
        // Calculate the center of the tank
        float centerX = x + img.getWidth() / 2;
        float centerY = y + img.getHeight() / 2;

        // Calculate the offset from the center to the muzzle
        float muzzleOffsetX = (float) (img.getWidth() / 2 * Math.cos(Math.toRadians(angle)));
        float muzzleOffsetY = (float) (img.getHeight() / 2 * Math.sin(Math.toRadians(angle)));

        // Calculate bullet start position based on tank's current position and direction
        float bulletStartX = centerX + muzzleOffsetX;
        float bulletStartY = centerY + muzzleOffsetY;

        bullets.add(new Bullet(bulletStartX, bulletStartY, angle, bulletImg));
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    void update(Tank otherTank) {
        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }

        for (Bullet bullet : bullets) {
            bullet.update();
        }

        // Handle collision with the other tank
        this.handleCollision(otherTank);

    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx =  Math.round(R * Math.cos(Math.toRadians(angle)));
        vy =  Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
       checkBorder();
    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
    }


    private void checkBorder() {

        int tankWidth = this.img.getWidth();
        int tankHeight = this.img.getHeight();

        if (x < 0) {
            x = 0;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - tankWidth) {
            x = GameConstants.GAME_WORLD_WIDTH - tankWidth;
        }
        if (y < 0) {
            y = 0;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - tankHeight) {
            y = GameConstants.GAME_WORLD_HEIGHT - tankHeight;
        }
    }

    public boolean checkCollision(Wall wall) {
        Rectangle tankRect = new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
        Rectangle wallRect = new Rectangle((int) wall.getX(), (int) wall.getY(), (int) wall.getWidth(), (int) wall.getHeight());
        return tankRect.intersects(wallRect);
    }


    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }



    void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
//        g2d.setColor(Color.RED);
//        g2d.rotate(Math.toRadians(angle), bounds.x + bounds.width/2, bounds.y + bounds.height/2);
//        g2d.drawRect((int)x,(int)y,this.img.getWidth(), this.img.getHeight());

        for (Bullet bullet : bullets) {
            bullet.drawImage(g);
        }

    }
}
