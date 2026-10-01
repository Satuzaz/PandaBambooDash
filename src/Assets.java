import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;

public final class Assets {
    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Path root;

    public Assets() throws Exception {
        Path found = findRoot(Paths.get("").toAbsolutePath());
        if (found == null) {
            found = findRoot(Paths.get(Assets.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
        }
        if (found == null) throw new IOException("Cannot find assets directory. Keep assets beside src and run from the project folder.");
        root = found;
        for (String path : new String[]{"player/panda.png", "enemy/snake.png", "item/bamboo.png",
                "background/level1.png", "background/level2.png", "background/level3.png",
                "ui/logo.png", "ui/heart_full.png", "ui/heart_empty.png",
                "ui/button_start.png", "ui/button_retry.png", "ui/button_menu.png",
                "ui/button_howtoplay.png", "ui/button_exit.png", "ui/button_next.png",
                "player/animation/idle.png", "player/animation/walk.png", "player/animation/jump.png",
                "enemy/animation/idle.png", "enemy/animation/move.png"}) {
            BufferedImage image = ImageIO.read(root.resolve(path).toFile());
            if (image == null) throw new IOException("Invalid PNG: " + path);
            images.put(path, image);
        }
        // Named frame images share the existing PNG sheets; originals remain untouched.
        BufferedImage[] idle = frames("enemy/animation/idle.png",4);
        BufferedImage[] move = frames("enemy/animation/move.png",4);
        images.put("snake_idle_1",idle[0]); images.put("snake_idle_2",idle[1]);
        images.put("snake_move_1",move[0]); images.put("snake_move_2",move[2]);
    }

    private Path findRoot(Path from) {
        for (Path p = from; p != null; p = p.getParent()) {
            if (Files.isDirectory(p.resolve("assets"))) return p.resolve("assets");
        }
        return null;
    }

    public BufferedImage get(String path) {
        BufferedImage image = images.get(path);
        if (image == null) throw new IllegalArgumentException("Unknown asset: " + path);
        return image;
    }

    /** Split equal horizontal cells once at construction; no image IO inside the game loop. */
    public BufferedImage[] frames(String path, int count) {
        BufferedImage sheet = get(path);
        if (count <= 0 || sheet.getWidth() < count) throw new IllegalArgumentException("Invalid frame count");
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            int start = i * sheet.getWidth() / count;
            int end = (i + 1) * sheet.getWidth() / count;
            frames[i] = sheet.getSubimage(start,0,end-start,sheet.getHeight());
        }
        return frames;
    }
}
