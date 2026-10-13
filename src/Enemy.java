import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {
    private int leftLimit;   // งูเดินไปทางซ้ายได้ไกลสุดถึงตรงนี้
    private int rightLimit;  // งูเดินไปทางขวาได้ไกลสุดถึงตรงนี้
    private double speed;
    private int direction = 1; // 1 = เดินขวา, -1 = เดินซ้าย
    private Animation animation;

    public Enemy(int x, int y, int leftLimit, int rightLimit, double speed, Assets assets) {
        super(x, y, 44, 30, null);
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.speed = speed;
        BufferedImage[] pictures = assets.snakeMove;
        if (speed == 0) {
            pictures = assets.snakeIdle;
        }
        // รูปงูใหญ่ขึ้น แต่กรอบชนยังเท่าเดิม
        animation = new Animation(pictures, 0.25, true);
    }

    @Override
    public void update(double dt) {
        animation.update(dt);
        x = x + direction * speed * dt;
        if (x < leftLimit) {
            x = leftLimit;
            direction = 1;
        }
        if (x + width > rightLimit) {
            x = rightLimit - width;
            direction = -1;
        }
    }

    @Override
    public void draw(Graphics g) {
        // รูปต้นฉบับหันซ้าย ถ้างูเดินขวาให้กลับด้านรูป
        drawFrame(g, animation.getFrame(), direction > 0);
    }

    public void checkCollision(Player player) {
        if (collidesWith(player)) {
            player.takeDamage();
        }
    }
}
