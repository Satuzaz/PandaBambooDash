import java.awt.*;
import java.awt.image.BufferedImage;

public class UIManager {
    private static final Color INK = new Color(48, 76, 30);
    private static final String MOVE = "A/D or Left/Right: Move";
    private static final String JUMP = "W / Up / SPACE: Jump";
    private BufferedImage menuBackground;
    private BufferedImage logo;
    private BufferedImage fullHeart;
    private BufferedImage emptyHeart;
    private BufferedImage bamboo;

    public UIManager(Assets assets) {
        menuBackground = Assets.resize(assets.backgrounds[0], 1280, 720);
        logo = Assets.fit(assets.logo, 580, 170);
        fullHeart = Assets.fit(assets.heartFull, 30, 30);
        emptyHeart = Assets.fit(assets.heartEmpty, 30, 30);
        bamboo = Assets.fit(assets.bamboo, 27, 35);
    }

    // กรอบปุ่มใช้ทั้งตอนวาดและตอนตรวจว่าคลิกโดนหรือไม่
    public static Rectangle bounds(String name) {
        switch (name) {
            case "start": return new Rectangle(480, 350, 320, 58);
            case "help": return new Rectangle(480, 426, 320, 58);
            case "exit": return new Rectangle(480, 502, 320, 58);
            case "retry": return new Rectangle(480, 390, 320, 58);
            case "menu": return new Rectangle(480, 466, 320, 58);
            default: throw new IllegalArgumentException("Unknown button: " + name);
        }
    }

    private void text(Graphics g, String value, int size, int y, Color color) {
        g.setFont(new Font("SansSerif", Font.BOLD, size));
        g.setColor(color);
        int x = (GamePanel.SCREEN_WIDTH - g.getFontMetrics().stringWidth(value)) / 2;
        g.drawString(value, x, y);
    }

    private void fit(Graphics g, BufferedImage image, int x, int y, int width, int height) {
        // รูปย่อไว้แล้ว ตรงนี้แค่วางให้อยู่กลางกรอบ
        g.drawImage(image, x + (width - image.getWidth()) / 2,
                y + (height - image.getHeight()) / 2, null);
    }

    private void overlay(Graphics g) {
        g.setColor(new Color(8, 28, 19, 160));
        g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
    }

    private void card(Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(255, 249, 216));
        g.fillRect(x, y, width, height);
        g.setColor(INK);
        g.drawRect(x, y, width, height);
    }

    private void button(Graphics g, String name, String label) {
        Rectangle button = bounds(name);
        g.setColor(new Color(100, 139, 47));
        g.fillRect(button.x, button.y, button.width, button.height);
        text(g, label, 22, button.y + 37, new Color(255, 248, 213));
    }

    public void renderMainMenu(Graphics g, boolean help) {
        g.drawImage(menuBackground, 0, 0, null);
        card(g, 330, 40, 620, 230);
        fit(g, logo, 350, 50, 580, 170);
        text(g, "PANDA BAMBOO DASH", 30, 245, INK);
        card(g, 424, 283, 432, 301);
        text(g, "LET'S GO, LITTLE PANDA!", 16, 325, INK);
        button(g, "start", "START GAME");
        button(g, "help", "HOW TO PLAY");
        button(g, "exit", "EXIT");
        card(g, 405, 612, 470, 82);
        text(g, MOVE, 16, 645, INK);
        text(g, JUMP, 16, 672, INK);
        if (help) {
            renderHelp(g);
        }
    }

    private void renderHelp(Graphics g) {
        overlay(g);
        card(g, 325, 145, 630, 420);
        text(g, "HOW TO PLAY", 34, 211, INK);
        text(g, MOVE, 19, 267, INK);
        text(g, JUMP, 19, 300, INK);
        text(g, "Collect 5 bamboo in each of the 3 levels.", 18, 350, INK);
        text(g, "Avoid snakes and pits. Keep your 4 hearts!", 18, 386, INK);
        text(g, "Finish Level 3 to win!", 18, 421, INK);
        button(g, "menu", "MAIN MENU");
    }

    public void renderHUD(Graphics g, Player player, Level level) {
        g.setColor(new Color(19, 53, 31));
        g.fillRect(24, 16, 1232, 48);
        for (int i = 0; i < Player.MAX_HEARTS; i++) {
            BufferedImage heart = emptyHeart;
            if (i < player.getHearts()) {
                heart = fullHeart;
            }
            fit(g, heart, 44 + 38 * i, 25, 30, 30);
        }
        text(g, "Level " + level.getNumber() + "/3 - " + level.getName(), 19, 47, Color.WHITE);
        fit(g, bamboo, 1131, 22, 27, 35);
        g.drawString(player.getBamboo() + " / 5", 1173, 47);
        g.setColor(new Color(19, 53, 31));
        g.fillRect(24, 670, 455, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString(MOVE + "  |  " + JUMP, 38, 690);
    }

    public void renderGameOver(Graphics g) {
        result(g, "GAME OVER", "Take a breath. Let's try again!");
    }

    public void renderWin(Graphics g) {
        result(g, "YOU WIN!", "All 3 levels complete. Well done!");
    }

    private void result(Graphics g, String title, String message) {
        overlay(g);
        card(g, 350, 152, 580, 416);
        text(g, title, 44, 298, INK);
        text(g, message, 18, 342, INK);
        button(g, "retry", "RETRY");
        button(g, "menu", "MAIN MENU");
    }
}
