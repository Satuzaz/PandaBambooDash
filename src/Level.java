import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// เก็บของทุกอย่างในด่าน: พื้น งู และไผ่ แล้วสั่งอัปเดตและวาดทั้งหมด
public class Level {
    private int number;
    private String name;
    private BufferedImage background;
    private ArrayList<Platform> platforms = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Bamboo> bamboos = new ArrayList<>();
    private Assets assets;

    public Level(int number, Assets assets) {
        this.number = number;
        this.assets = assets;
        // ด่าน 1 ใช้รูป level1.png, ด่าน 2 ใช้ level2.png แต่อาร์เรย์เริ่มที่ช่อง 0
        background = assets.backgrounds[number - 1];

        // พื้นด้านล่างและพื้นลอย (x, y, กว้าง, สูง)
        // ไผ่ (x, y)
        // งู (x, y, ขอบซ้าย, ขอบขวา, ความเร็ว) ความเร็ว 0 = งูอยู่นิ่ง
        if (number == 1) {
            name = "Bamboo Forest";
            platform(0, 530, 800, 70);
            platform(180, 435, 150, 20);
            platform(410, 345, 150, 20);
            bamboo(110, 488);
            bamboo(240, 393);
            bamboo(470, 303);
            bamboo(630, 488);
            bamboo(740, 488);
            snake(390, 500, 350, 720, 56.25);
        } else if (number == 2) {
            name = "Deep Forest";
            platform(0, 530, 800, 70);
            platform(120, 440, 120, 20);
            platform(290, 350, 130, 20);
            platform(470, 260, 130, 20);
            platform(640, 350, 130, 20);
            bamboo(160, 398);
            bamboo(340, 308);
            bamboo(520, 218);
            bamboo(695, 308);
            bamboo(740, 488);
            snake(280, 500, 260, 590, 82.5);
            snake(490, 230, 470, 600, 48.75);
        } else {
            name = "Hidden Cave";
            // พื้นด้านล่างแบ่งเป็น 3 ชิ้น ช่องว่างระหว่างพื้นคือหลุม
            platform(0, 530, 230, 70);
            platform(330, 530, 180, 70);
            platform(620, 530, 180, 70);
            platform(130, 435, 100, 20);
            platform(275, 345, 100, 20);
            platform(430, 255, 100, 20);
            platform(595, 345, 100, 20);
            bamboo(165, 393);
            bamboo(310, 303);
            bamboo(465, 213);
            bamboo(630, 303);
            bamboo(750, 488);
            snake(350, 500, 330, 510, 105);
            snake(640, 500, 620, 800, 116.25);
            snake(440, 225, 430, 530, 56.25);
        }
    }

    private void platform(int x, int y, int width, int height) {
        Color color = new Color(124, 173, 73);
        if (number == 3) {
            color = new Color(77, 158, 167);
        }
        platforms.add(new Platform(x, y, width, height, color));
    }

    private void bamboo(int x, int y) {
        bamboos.add(new Bamboo(x, y, assets));
    }

    private void snake(int x, int y, int leftLimit, int rightLimit, double speed) {
        enemies.add(new Enemy(x, y, leftLimit, rightLimit, speed, assets));
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public void update(double dt, Player player) {
        for (Enemy enemy : enemies) {
            enemy.update(dt);
            enemy.checkCollision(player);
        }
        for (Bamboo bamboo : bamboos) {
            if (!bamboo.isCollected() && bamboo.collidesWith(player)) {
                bamboo.collect(player);
            }
        }
    }

    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, null);
        // ด่าน 3 มีหนามสามเหลี่ยมสีแดงอยู่ในหลุม
        if (number == 3) {
            g.setColor(new Color(216, 93, 69));
            for (int x = 230; x < 330; x += 20) {
                g.fillPolygon(new int[]{GamePanel.screenX(x), GamePanel.screenX(x + 10), GamePanel.screenX(x + 20)},
                        new int[]{GamePanel.screenY(599), GamePanel.screenY(578), GamePanel.screenY(599)}, 3);
            }
            for (int x = 510; x < 620; x += 20) {
                g.fillPolygon(new int[]{GamePanel.screenX(x), GamePanel.screenX(x + 10), GamePanel.screenX(x + 20)},
                        new int[]{GamePanel.screenY(599), GamePanel.screenY(578), GamePanel.screenY(599)}, 3);
            }
        }
        for (Platform platform : platforms) {
            platform.draw(g);
        }
        for (Bamboo bamboo : bamboos) {
            bamboo.draw(g);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
    }
}
