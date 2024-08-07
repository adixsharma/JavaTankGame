package src.tankProgram.game;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class Bullet {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private BufferedImage img; // Image for the bullet
    private boolean destroyed; // Indicates if the bullet has been destroyed

    // Constructor for each bullet object sets some default values
    public Bullet(float x, float y, float angle, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.img = img;
        this.vx = (float) Math.cos(Math.toRadians(angle)) * 10; // Set speed of bullet
        this.vy = (float) Math.sin(Math.toRadians(angle)) * 10; // Set speed of bullet
        this.destroyed = false;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    // Getters and Setters
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }


    // Draw the bullet on the screen
    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
    }


}
