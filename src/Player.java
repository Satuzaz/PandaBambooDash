import java.awt.Graphics;
import java.util.ArrayList;

public class Player extends Entity {
    private double velocityX;
    private double velocityY;
    public static final int MAX_HEARTS = 4;
    private static final double WALK_SPEED = 245;     // ความเร็วเดิน (หน่วยต่อวินาที)
    private static final double JUMP_SPEED = 610;     // ความเร็วตอนเริ่มกระโดด
    private static final double GRAVITY = 1600;       // แรงโน้มถ่วง ดึงลงทุกวินาที
    private static final double MAX_FALL_SPEED = 900; // ตกเร็วสุดได้เท่านี้
    private static final double DAMAGE_COOLDOWN = 1.4; // เวลาที่ไม่เสียหัวใจซ้ำหลังโดนชน (วินาที)
    private int hearts = MAX_HEARTS;
    private Animation idleAnimation;
    private Animation walkAnimation;
    private Animation jumpAnimation;
    private Animation currentAnimation;
    private boolean facingLeft;
    private int bambooCount;
    private boolean onGround;
    private double damageCooldown;
    private double blinkTimer;
    private boolean hidden;
    private ArrayList<Platform> platforms = new ArrayList<>();

    public Player(Assets assets) {
        super(40, 482, 40, 48, null);
        idleAnimation = new Animation(assets.pandaIdle, 0.20, true);
        walkAnimation = new Animation(assets.pandaWalk, 0.12, true);
        jumpAnimation = new Animation(assets.pandaJump, 0.12, false);
        currentAnimation = idleAnimation;
    }

    public int getHearts() {
        return hearts;
    }

    public int getBambooCount() {
        return bambooCount;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void move(int direction) {
        velocityX = direction * WALK_SPEED;
        if (direction != 0) {
            facingLeft = direction < 0;
        }
    }

    public void jump() {
        if (onGround) {
            velocityY = -JUMP_SPEED; // ค่าลบ = ขึ้น เพราะแกน y ของจอชี้ลง
            onGround = false;
        }
    }

    public void collectBamboo() {
        bambooCount++;
    }

    public void enterLevel(Level level) {
        platforms = level.getPlatforms();
        bambooCount = 0;
        respawn();
    }

    private void respawn() {
        x = 40;
        y = 482;
        velocityX = 0;
        velocityY = 0;
        onGround = false;
    }

    public void takeDamage() {
        if (damageCooldown <= 0 && hearts > 0) {
            hearts--;
            damageCooldown = DAMAGE_COOLDOWN;
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
        // โลกของเกมกว้าง 800 สูง 600 ห้ามเดินออกนอกขอบซ้ายขวา
        if (x + width > 800) {
            x = 800 - width;
        }

        // เพิ่มแรงตก แล้วตรวจว่าลงบนพื้นหรือชนเพดาน
        velocityY = velocityY + GRAVITY * dt;
        if (velocityY > MAX_FALL_SPEED) {
            velocityY = MAX_FALL_SPEED;
        }
        y = y + velocityY * dt;
        onGround = false;
        for (Platform platform : platforms) {
            if (collidesWith(platform)) {
                if (velocityY > 0) {
                    y = platform.getY() - height;
                    onGround = true;
                } else if (velocityY < 0) {
                    y = platform.getY() + platform.getHeight();
                }
                velocityY = 0;
            }
        }
        // ตกลงไปต่ำกว่าขอบล่างของจอ = ตกหลุม
        if (y > 600) {
            takeDamage();
            respawn();
        }
        // เลือกท่า: ลอยอยู่ = กระโดด, เดินอยู่ = เดิน, นอกนั้น = ยืน
        Animation nextAnimation = idleAnimation;
        if (!onGround) {
            nextAnimation = jumpAnimation;
        } else if (velocityX != 0) {
            nextAnimation = walkAnimation;
        }
        if (currentAnimation != nextAnimation) {
            currentAnimation = nextAnimation;
            currentAnimation.reset();
        }
        currentAnimation.update(dt);
    }

    @Override
    public void draw(Graphics g) {
        // ถ้าอยู่ในจังหวะซ่อนตัว ไม่ต้องวาดรูป
        if (hidden) {
            return;
        }
        drawFrame(g, currentAnimation.getFrame(), facingLeft);
    }
}
