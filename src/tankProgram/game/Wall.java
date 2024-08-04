package src.tankProgram.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall {
    private float x;
    private float y;
    private float width;
    private float height;
    private boolean isDestructible;
    private BufferedImage wallImg;
    private boolean isDestroyed = false;

    private boolean isDoubleDamage; // first powerup
    private boolean isHealthBoost;
    private boolean halfHealth; // third power "up" (down)

    private BufferedImage powerUpImg;
    private boolean hasPowerUp; // Indicates if the wall contains a power-up
    private boolean powerUpVisible;



    public Wall(float x, float y, float width, float height, boolean isDestructible, BufferedImage wallImg) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isDestructible = isDestructible;
        this.wallImg = wallImg;
        this.isDoubleDamage = false;
        this.isHealthBoost = false;
        this.halfHealth = false;
    }

    public boolean isDoubleDamage() {
        return isDoubleDamage;
    }
    public void setDoubleDamage(boolean doubleDamage) {
        isDoubleDamage = doubleDamage;
    }

    public boolean isHalfHealth() {
        return halfHealth;
    }

    public void setHalfHealth(boolean halfHealth) {
        this.halfHealth = halfHealth;
    }

    public boolean hasPowerUp() {
        return hasPowerUp;
    }

    public void setHasPowerUp(boolean hasPowerUp) {
        this.hasPowerUp = hasPowerUp;
    }

    public boolean isPowerUpVisible() {
        return powerUpVisible;
    }

    public void setPowerUpVisible(boolean powerUpVisible) {
        this.powerUpVisible = powerUpVisible;
    }

    public boolean isHealthBoost() {
        return isHealthBoost;
    }

    public void setHealthBoost(boolean healthBoost) {
        isHealthBoost = healthBoost;
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }
    public boolean isDestroyed() {
        return isDestroyed;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    public boolean isDestructible() {
        return isDestructible;
    }
    public BufferedImage getWallImg() {
        return wallImg;
    }

    public BufferedImage getPowerUpImg() {
        return powerUpImg;
    }

    public void setPowerUpImg(BufferedImage powerUpImg) {
        this.powerUpImg = powerUpImg;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    void drawImage(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (!isDestroyed) {
            g2.drawImage(wallImg, (int) x, (int) y, (int) width, (int) height, null);
        } else if (isDoubleDamage && powerUpImg != null) {
            g2.drawImage(powerUpImg, (int) x, (int) y, (int) width, (int) height, null);
        }else if (halfHealth && powerUpImg != null && powerUpVisible) {
            g2.drawImage(powerUpImg, (int) x, (int) y, (int) width, (int) height, null);
        }else if (isHealthBoost && powerUpImg != null && powerUpVisible) {
            g2.drawImage(powerUpImg, (int) x, (int) y, (int) width, (int) height, null);
        }
    }


}
