import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Item extends Entity {
    private boolean collected;

    protected Item(double x, double y, int width, int height, BufferedImage image) {
        super(x, y, width, height, image);
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect(Player player) {
        if (!collected) {
            collected = true;
            player.collectBamboo();
        }
    }



@Override
    public void draw(Graphics g) {
        if (!collected) {
            super.draw(g);
        }
    }
}
