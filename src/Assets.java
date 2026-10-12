import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// โหลดรูปครั้งเดียว แล้วเก็บไว้ในตัวแปรที่เรียกใช้ได้โดยตรง
public class Assets {
    public BufferedImage bamboo;
    public BufferedImage logo;
    public BufferedImage heartFull;
    public BufferedImage heartEmpty;
    public BufferedImage[] backgrounds = new BufferedImage[3];
    public BufferedImage[] pandaIdle;
    public BufferedImage[] pandaWalk;
    public BufferedImage[] pandaJump;
    public BufferedImage[] snakeIdle;
    public BufferedImage[] snakeMove;
    private File folder;

    public Assets() throws IOException {
        folder = findFolder();
        bamboo = load("item/bamboo.png");
        logo = load("ui/logo.png");
        heartFull = load("ui/heart_full.png");
        heartEmpty = load("ui/heart_empty.png");
        for (int i = 0; i < 3; i++) {
            backgrounds[i] = load("background/level" + (i + 1) + ".png");
        }
        pandaIdle = loadFrames("player/animation/idle.png", 48, 64);
        pandaWalk = loadFrames("player/animation/walk.png", 48, 64);
        pandaJump = loadFrames("player/animation/jump.png", 48, 64);
        snakeIdle = loadFrames("enemy/animation/idle.png", 80, 72);
        snakeMove = loadFrames("enemy/animation/move.png", 80, 72);
    }

    private BufferedImage load(String name) throws IOException {
        File file = new File(folder, name);
        if (!file.isFile()) {
            throw new IOException("Missing image: " + file.getAbsolutePath());
        }
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("Invalid image: " + file.getAbsolutePath());
        }
        return image;
    }

    // หา assets จากโฟลเดอร์ที่รันก่อน แล้วลองหาจากตำแหน่งไฟล์คลาส
    // เก็บส่วนนี้ไว้เพื่อให้เปิดเกมจาก IDE หรือต่างโฟลเดอร์ได้
    private File findFolder() throws IOException {
        File result = searchParents(new File(System.getProperty("user.dir")));
        if (result != null) {
            return result;
        }
        try {
            File classFolder = new File(Assets.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            result = searchParents(classFolder);
        } catch (java.net.URISyntaxException e) {
            throw new IOException("Cannot locate game folder", e);
        }
        if (result == null) {
            throw new IOException("Cannot find assets. Keep assets beside src.");
        }
        return result;
    }

    private File searchParents(File start) {
        while (start != null) {
            File candidate = new File(start, "assets");
            if (candidate.isDirectory()) {
                return candidate;
            }
            start = start.getParentFile();
        }
        return null;
    }

    private BufferedImage[] loadFrames(String name, int width, int height) throws IOException {
        BufferedImage sheet = load(name);
        BufferedImage[] pictures = new BufferedImage[4];
        int cellWidth = sheet.getWidth() / 4;

        // หาขอบล่างของรูป ไม่เอาพื้นที่โปร่งใสใต้เท้า
        int bottom = 0;
        for (int y = 0; y < sheet.getHeight(); y++) {
            for (int x = 0; x < sheet.getWidth(); x++) {
                Color pixel = new Color(sheet.getRGB(x, y), true);
                if (pixel.getAlpha() > 32) {
                    bottom = y;
                    break;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            BufferedImage picture = sheet.getSubimage(i * cellWidth, 0, cellWidth, bottom + 1);
            pictures[i] = fit(picture, GamePanel.screenX(width), GamePanel.screenY(height));
        }
        return pictures;
    }

    // ย่อรูปตอนโหลด เพื่อไม่ต้องย่อซ้ำทุกครั้งที่วาด
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return result;
    }

    public static BufferedImage fit(BufferedImage image, int width, int height) {
        double scale = (double) width / image.getWidth();
        double heightScale = (double) height / image.getHeight();
        if (heightScale < scale) {
            scale = heightScale;
        }
        int newWidth = Math.max(1, (int) (image.getWidth() * scale));
        int newHeight = Math.max(1, (int) (image.getHeight() * scale));
        return resize(image, newWidth, newHeight);
    }
}
