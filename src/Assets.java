import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// โหลดรูปครั้งเดียว แล้วเก็บไว้ในตัวแปรที่เรียกใช้ได้โดยตรง
// รูปทุกไฟล์ในโฟลเดอร์ assets ถูกทำให้มีขนาดเท่ากับที่ใช้วาดในเกมแล้ว จึงไม่ต้องย่อรูปในโค้ด
public class Assets {
    public BufferedImage bamboo;
    public BufferedImage bambooSmall;
    public BufferedImage logo;
    public BufferedImage heartFull;
    public BufferedImage heartEmpty;
    public BufferedImage menuBackground;
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
        bambooSmall = load("ui/bamboo_small.png");
        logo = load("ui/logo.png");
        heartFull = load("ui/heart_full.png");
        heartEmpty = load("ui/heart_empty.png");
        menuBackground = load("background/menu.png");
        for (int i = 0; i < 3; i++) {
            backgrounds[i] = load("background/level" + (i + 1) + ".png");
        }
        pandaIdle = loadFrames("player/animation/idle");
        pandaWalk = loadFrames("player/animation/walk");
        pandaJump = loadFrames("player/animation/jump");
        snakeIdle = loadFrames("enemy/animation/idle");
        snakeMove = loadFrames("enemy/animation/move");
    }

    // หาโฟลเดอร์ assets ถ้ารันจากโฟลเดอร์โปรเจกต์จะเจอที่ "assets"
    // ถ้ารันจากในโฟลเดอร์ src จะต้องถอยออกไปหนึ่งชั้นเป็น "../assets"
    private File findFolder() {
        File result = new File("assets");
        if (!result.isDirectory()) {
            result = new File("../assets");
        }
        return result;
    }

    private BufferedImage load(String name) throws IOException {
        return ImageIO.read(new File(folder, name));
    }

    // แอนิเมชันหนึ่งท่ามี 4 รูป ชื่อไฟล์ลงท้ายด้วย 1 ถึง 4 เช่น idle1.png ... idle4.png
    private BufferedImage[] loadFrames(String name) throws IOException {
        BufferedImage[] pictures = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            pictures[i] = load(name + (i + 1) + ".png");
        }
        return pictures;
    }
}
