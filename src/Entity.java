import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Entity {
    private double x, y;
    private final int width, height;
    private final BufferedImage image;

    protected Entity(double x, double y, int width, int height, BufferedImage image) {
        this.x = x; this.y = y; this.width = width; this.height = height; this.image = image;
    }
    public final double getX() { return x; }
    public final double getY() { return y; }
    public final int getWidth() { return width; }
    public final int getHeight() { return height; }
    protected final void setX(double x) { this.x = x; }
    protected final void setY(double y) { this.y = y; }
    public final Rectangle2D.Double getBounds() { return new Rectangle2D.Double(x, y, width, height); }
    public final boolean collidesWith(Entity other) { return getBounds().intersects(other.getBounds()); }
    public abstract void update(double dt);
    public void draw(Graphics2D g) {
        if (image != null) g.drawImage(image, (int)x, (int)y, width, height, null);
    }
}
