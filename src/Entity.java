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
        if (image == null) {
            this.image = null;
        } else {
            this.image = Assets.fit(image, GamePanel.screenX(width), GamePanel.screenY(height));
        }
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

protected void setY(double y) {
        this.y = y;
    }

    // สี่เหลี่ยมชนกันเมื่อขอบของทั้งสองรูปซ้อนกันทั้งแนวนอนและแนวตั้ง
    public boolean collidesWith(Entity other) {
        return x < other.x + other.width && x + width > other.x
                && y < other.y + other.height && y + height > other.y;
    }

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
