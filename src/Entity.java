import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Entity {
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    private BufferedImage image;

    protected Entity(double x, double y, int width, int height, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // สี่เหลี่ยมชนกันเมื่อขอบของทั้งสองรูปซ้อนกันทั้งแนวนอนและแนวตั้ง
    public boolean collidesWith(Entity other) {
        return x < other.x + other.width && x + width > other.x
                && y < other.y + other.height && y + height > other.y;
    }

    // dt (delta time) = เวลาที่ผ่านไปตั้งแต่เฟรมก่อน หน่วยเป็นวินาที (เกมนี้ใช้ 0.016)
    // ระยะที่เคลื่อนที่ = ความเร็ว x dt
    public void update(double dt) {
        // คลาสลูกที่เคลื่อนที่ได้จะเขียน update ของตัวเอง
    }

    // วางภาพกลางกรอบชน และให้เท้าอยู่ที่ขอบล่าง
    protected void drawFrame(Graphics g, BufferedImage frame, boolean flip) {
        int drawX = GamePanel.screenX(x) + (GamePanel.screenX(width) - frame.getWidth()) / 2;
        int drawY = GamePanel.screenY(y + height) - frame.getHeight();
        if (flip) {
            g.drawImage(frame, drawX + frame.getWidth(), drawY, -frame.getWidth(), frame.getHeight(), null);
        } else {
            g.drawImage(frame, drawX, drawY, null);
        }
    }

    public void draw(Graphics g) {
        if (image != null) {
            drawFrame(g, image, false);
        }
    }
}
