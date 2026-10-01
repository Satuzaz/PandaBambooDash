import java.awt.*;

public final class Platform extends Entity {
    private final Color color;
    public Platform(int x, int y, int width, int height, Color color) {
        super(x, y, width, height, null); this.color = color;
    }
    @Override public void update(double dt) { }
    @Override public void draw(Graphics2D g) {
        g.setColor(new Color(24, 32, 31));
        g.fillRoundRect((int)getX(), (int)getY(), getWidth(), getHeight(), 8, 8);
        g.setColor(color);
        g.fillRoundRect((int)getX(), (int)getY(), getWidth(), 9, 8, 8);
        g.setColor(new Color(255, 255, 255, 35));
        g.drawLine((int)getX()+5, (int)getY()+2, (int)getX()+getWidth()-5, (int)getY()+2);
    }
}
