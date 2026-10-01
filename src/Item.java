import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Item extends Entity {
    private boolean collected;
    protected Item(double x, double y, int w, int h, BufferedImage image) { super(x,y,w,h,image); }
    public final boolean isCollected() { return collected; }
    public final void collect(Player player) {
        if (!collected) { collected = true; onCollect(player); }
    }
    protected abstract void onCollect(Player player);
    @Override public void update(double dt) { }
    @Override public void draw(Graphics2D g) { if (!collected) super.draw(g); }
}
