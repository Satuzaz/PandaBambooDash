import java.awt.Graphics;
import java.util.ArrayList;


public class Player extends Entity {
    private double velocityX;
    private double velocityY;
    private double speed = 245;
    public static final int MAX_HEARTS = 4;
    private int hearts = MAX_HEARTS;
    private Animation idle;
    private Animation walk;
    private Animation jump;
    private Animation animation;
    private boolean facingLeft;
    private int bamboo;
    private boolean grounded;
    private double damageCooldown;
    private double blinkTimer;
    private boolean hidden;
    private ArrayList<Platform> platforms = new ArrayList<>();

    public Player(Assets assets) {
        super(40, 482, 40, 48, null);
        idle = new Animation(assets.pandaIdle, 0.20, true);
        walk = new Animation(assets.pandaWalk, 0.12, true);
        jump = new Animation(assets.pandaJump, 0.12, false);
        animation = idle;
    }

    public int getHearts() {
        return hearts;
    }

    public int getBamboo() {
        return bamboo;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void move(int direction) {
        velocityX = direction * speed;
        if (direction != 0) {
            facingLeft = direction < 0;
        }
    }

    public void jump() {
        if (grounded) {
            velocityY = -610;
            grounded = false;
        }
    }

    public void collectBamboo() {
        bamboo++;
    }

    public void enterLevel(Level level) {
        platforms = level.getPlatforms();
        bamboo = 0;
        respawn();
    }

    private void respawn() {
        x = 40;
        y = 482;
        velocityX = 0;
        velocityY = 0;
        grounded = false;
    }

    public void takeDamage() {
        if (damageCooldown <= 0 && hearts > 0) {
            hearts--;
            damageCooldown = 1.4;
            blinkTimer = 0;
            hidden = false;
        }
    }

    @Override
    public void update(double dt) {
        // หลังโดนชน ให้สลับแสดงและซ่อนตัวทุก 0.1 วินาที
        if (damageCooldown > 0) {
            damageCooldown = damageCooldown - dt;
            blinkTimer = blinkTimer + dt;
            if (blinkTimer >= 0.1) {
                hidden = !hidden;
                blinkTimer = 0;
            }
        }
        if (damageCooldown <= 0) {
            hidden = false;
        }

        // ขยับแนวนอน แล้วตรวจว่าชนด้านข้างของพื้นหรือไม่
        x = x + velocityX * dt;
        for (Platform platform : platforms) {
            if (collidesWith(platform)) {
                if (velocityX > 0) {
                    x = platform.getX() - width;
                } else if (velocityX < 0) {
                    x = platform.getX() + platform.getWidth();
                }
            }
        }
        if (x < 0) {
            x = 0;
        }
        if (x + width > 800) {
            x = 800 - width;
        }

        // เพิ่มแรงตก แล้วตรวจว่าลงบนพื้นหรือชนเพดาน
        velocityY = velocityY + 1600 * dt;
        if (velocityY > 900) {
            velocityY = 900;
        }
        y = y + velocityY * dt;
        grounded = false;
        for (Platform platform : platforms) {
            if (collidesWith(platform)) {
                if (velocityY > 0) {
                    y = platform.getY() - height;
                    grounded = true;
                } else if (velocityY < 0) {
                    y = platform.getY() + platform.getHeight();
                }
                velocityY = 0;
            }
        }
        if (y > 600) {
            takeDamage();
            respawn();
        }
        Animation next = idle;
        if (!grounded) {
            next = jump;
        } else if (velocityX != 0) {
            next = walk;
        }
        if (animation != next) {
            animation = next;
            animation.reset();
        }
        animation.update(dt);
    }

    @Override
    public void draw(Graphics g) {
        // ถ้าอยู่ในจังหวะซ่อนตัว ไม่ต้องวาดรูป
        if (hidden) {
            return;
        }
        drawFrame(g, animation.getFrame(), facingLeft);
    }
}
