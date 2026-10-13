import java.awt.*;
import java.awt.image.BufferedImage;

public class UIManager {
    private static final Color TEXT_COLOR = new Color(48, 76, 30);
    private static final String MOVE_TEXT = "A/D or Left/Right: Move";
    private static final String JUMP_TEXT = "W / Up / SPACE: Jump";
    private BufferedImage menuBackground;
    private BufferedImage logo;
    private BufferedImage fullHeart;
    private BufferedImage emptyHeart;
    private BufferedImage bambooIcon;

    public UIManager(Assets assets) {
        menuBackground = assets.menuBackground;
        logo = assets.logo;
        fullHeart = assets.heartFull;
        emptyHeart = assets.heartEmpty;
        bambooIcon = assets.bambooSmall;
    }

    // กรอบปุ่ม (x, y, กว้าง, สูง) ใช้ทั้งตอนวาดและตอนตรวจว่าคลิกโดนหรือไม่
    public static final Rectangle START_BUTTON = new Rectangle(480, 350, 320, 58);
    public static final Rectangle HELP_BUTTON = new Rectangle(480, 426, 320, 58);
    public static final Rectangle EXIT_BUTTON = new Rectangle(480, 502, 320, 58);
    public static final Rectangle RETRY_BUTTON = new Rectangle(480, 390, 320, 58);
    public static final Rectangle MENU_BUTTON = new Rectangle(480, 466, 320, 58);

    // เขียนข้อความให้อยู่กึ่งกลางจอในแนวนอน ที่ความสูง y
    private void drawText(Graphics g, String value, int size, int y, Color color) {
        g.setFont(new Font("SansSerif", Font.BOLD, size));
        g.setColor(color);
        int x = (GamePanel.SCREEN_WIDTH - g.getFontMetrics().stringWidth(value)) / 2;
        g.drawString(value, x, y);
    }

    // วางรูปให้อยู่กลางกรอบ (x, y, width, height)
    private void drawImageCenter(Graphics g, BufferedImage image, int x, int y, int width, int height) {
        g.drawImage(image, x + (width - image.getWidth()) / 2,
                y + (height - image.getHeight()) / 2, null);
    }

    // ทับทั้งจอด้วยสีเข้มโปร่งแสง (ตัวเลขสุดท้าย 160 คือความทึบ)
    private void darkenScreen(Graphics g) {
        g.setColor(new Color(8, 28, 19, 160));
        g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
    }

    // กล่องพื้นสีครีม มีเส้นขอบ
    private void drawBox(Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(255, 249, 216));
        g.fillRect(x, y, width, height);
        g.setColor(TEXT_COLOR);
        g.drawRect(x, y, width, height);
    }

    private void drawButton(Graphics g, Rectangle button, String label) {
        g.setColor(new Color(100, 139, 47));
        g.fillRect(button.x, button.y, button.width, button.height);
        drawText(g, label, 22, button.y + 37, new Color(255, 248, 213));
    }

    public void renderMainMenu(Graphics g, boolean showingHelp) {
        g.drawImage(menuBackground, 0, 0, null);
        drawBox(g, 330, 40, 620, 230);
        drawImageCenter(g, logo, 350, 50, 580, 170);
        drawText(g, "PANDA BAMBOO DASH", 30, 245, TEXT_COLOR);
        drawBox(g, 424, 283, 432, 301);
        drawText(g, "LET'S GO, LITTLE PANDA!", 16, 325, TEXT_COLOR);
        drawButton(g, START_BUTTON, "START GAME");
        drawButton(g, HELP_BUTTON, "HOW TO PLAY");
        drawButton(g, EXIT_BUTTON, "EXIT");
        drawBox(g, 405, 612, 470, 82);
        drawText(g, MOVE_TEXT, 16, 645, TEXT_COLOR);
        drawText(g, JUMP_TEXT, 16, 672, TEXT_COLOR);
        if (showingHelp) {
            renderHelp(g);
        }
    }

    private void renderHelp(Graphics g) {
        darkenScreen(g);
        drawBox(g, 325, 145, 630, 420);
        drawText(g, "HOW TO PLAY", 34, 211, TEXT_COLOR);
        drawText(g, MOVE_TEXT, 19, 267, TEXT_COLOR);
        drawText(g, JUMP_TEXT, 19, 300, TEXT_COLOR);
        drawText(g, "Collect 5 bamboo in each of the 3 levels.", 18, 350, TEXT_COLOR);
        drawText(g, "Avoid snakes and pits. Keep your 4 hearts!", 18, 386, TEXT_COLOR);
        drawText(g, "Finish Level 3 to win!", 18, 421, TEXT_COLOR);
        drawButton(g, MENU_BUTTON, "MAIN MENU");
    }

    public void renderHUD(Graphics g, Player player, Level level) {
        g.setColor(new Color(19, 53, 31));
        g.fillRect(24, 16, 1232, 48);
        for (int i = 0; i < Player.MAX_HEARTS; i++) {
            BufferedImage heart = emptyHeart;
            if (i < player.getHearts()) {
                heart = fullHeart;
            }
            drawImageCenter(g, heart, 44 + 38 * i, 25, 30, 30);
        }
        drawText(g, "Level " + level.getNumber() + "/3 - " + level.getName(), 19, 47, Color.WHITE);
        drawImageCenter(g, bambooIcon, 1131, 22, 27, 35);
        g.drawString(player.getBambooCount() + " / 5", 1173, 47);
        g.setColor(new Color(19, 53, 31));
        g.fillRect(24, 670, 455, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString(MOVE_TEXT + "  |  " + JUMP_TEXT, 38, 690);
    }

    public void renderGameOver(Graphics g) {
        renderResult(g, "GAME OVER", "Take a breath. Let's try again!");
    }

    public void renderWin(Graphics g) {
        renderResult(g, "YOU WIN!", "All 3 levels complete. Well done!");
    }

    private void renderResult(Graphics g, String title, String message) {
        darkenScreen(g);
        drawBox(g, 350, 152, 580, 416);
        drawText(g, title, 44, 298, TEXT_COLOR);
        drawText(g, message, 18, 342, TEXT_COLOR);
        drawButton(g, RETRY_BUTTON, "RETRY");
        drawButton(g, MENU_BUTTON, "MAIN MENU");
    }
}
