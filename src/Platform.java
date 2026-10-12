import java.awt.Color;
import java.awt.Graphics;

public class Platform extends Entity {
    private Color color;

    public Platform(int x, int y, int width, int height, Color color) {
        super(x, y, width, height, null);
        this.color = color;
    }

@Override
    public void draw(Graphics g) {
        int x = GamePanel.screenX(getX());
        int y = GamePanel.screenY(getY());
        int width = GamePanel.screenX(getWidth());
        int height = GamePanel.screenY(getHeight());
        g.setColor(new Color(24, 32, 31));
        g.fillRect(x, y, width, height);
        g.setColor(color);
        g.fillRect(x, y, width, GamePanel.screenY(9));
    }
}
