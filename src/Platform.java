import java.awt.Color;
import java.awt.Graphics;

// พื้นที่แพนด้ายืนได้ วาดเป็นสี่เหลี่ยมสีเข้ม และมีแถบสีด้านบนเหมือนหญ้า
public class Platform extends Entity {
    private Color topColor;

    public Platform(int x, int y, int width, int height, Color topColor) {
        super(x, y, width, height, null);
        this.topColor = topColor;
    }

    @Override
    public void draw(Graphics g) {
        int drawX = GamePanel.screenX(x);
        int drawY = GamePanel.screenY(y);
        int drawWidth = GamePanel.screenX(width);
        int drawHeight = GamePanel.screenY(height);
        g.setColor(new Color(24, 32, 31));
        g.fillRect(drawX, drawY, drawWidth, drawHeight);
        g.setColor(topColor);
        g.fillRect(drawX, drawY, drawWidth, GamePanel.screenY(9));
    }
}
