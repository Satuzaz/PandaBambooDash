import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Level {
    private int number;
    private String name;
    private BufferedImage background;
    private ArrayList<Platform> platforms = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    protected Assets assets;

    protected Level(int number, String name, Assets assets) {
        this.number = number;
        this.name = name;
        this.assets = assets;
        BufferedImage original = assets.backgrounds[number - 1];
        // Keep decorative platforms outside the crop, as in the original level.
        int startX = 0;
        int endX = original.getWidth();
        int endY = original.getHeight();
        if (number == 1) {
            endY = original.getHeight() * 48 / 100;
        } else if (number == 2) {
            startX = original.getWidth() / 3;
            endX = original.getWidth() * 3 / 4;
            endY = original.getHeight() * 38 / 100;
        }
        BufferedImage crop = original.getSubimage(startX, 0, endX - startX, endY);
        background = Assets.resize(crop, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
    }

    protected void platform(int x, int y, int width, int height) {
        Color color = new Color(124, 173, 73);
        if (number == 3) {
            color = new Color(77, 158, 167);
        }
        platforms.add(new Platform(x, y, width, height, color));
    }

    protected void bamboo(int x, int y) {
        items.add(new Bamboo(x, y, assets));
    }

    protected void snake(int x, int y, int left, int right, double speed) {
        enemies.add(new Enemy(x, y, left, right, speed, assets));
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

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void update(double dt, Player player) {
        for (Enemy enemy : enemies) {
            enemy.update(dt);
            enemy.checkCollision(player);
        }
        for (Item item : items) {
            if (!item.isCollected() && item.collidesWith(player)) {
                item.collect(player);
            }
        }
    }

    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, null);
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
        for (Item item : items) {
            item.draw(g);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
    }
}
