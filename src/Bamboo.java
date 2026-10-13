import java.awt.Graphics;

// ไผ่ที่แพนด้าต้องเก็บ ขนาด 26 x 38 เก็บแล้วจะหายไป
public class Bamboo extends Entity {
    private boolean collected;

    public Bamboo(int x, int y, Assets assets) {
        super(x, y, 26, 38, assets.bamboo);
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
