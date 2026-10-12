import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AnimationTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static Object field(Object object, String name) throws Exception {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    private static int[] render(Entity entity) {
        BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        entity.draw(g);
        g.dispose();
        return image.getRGB(0, 0, 1280, 720, null, 0, 1280);
    }

    private static boolean visible(Entity entity) {
        for (int pixel : render(entity)) {
            if ((pixel >>> 24) != 0) return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        BufferedImage a = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        BufferedImage b = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        Animation loop = new Animation(new BufferedImage[]{a, b}, 0.1, true);
        loop.update(0.11);
        check(loop.getFrame() == b, "frame advances");
        loop.update(0.1);
        check(loop.getFrame() == a, "frames loop");
        Animation once = new Animation(new BufferedImage[]{a, b}, 0.1, false);
        once.update(1);
        check(once.getFrame() == b, "jump holds final frame");
        once.reset();
        check(once.getFrame() == a, "reset returns to first frame");

        Assets assets = new Assets();
        Player player = new Player(assets);
        player.enterLevel(new Level1(assets));
        player.update(0.016);
        check(player.getHearts() == 4, "four starting hearts");
        check(field(player, "animation") == field(player, "idle"), "standing animation");
        player.move(1);
        player.update(0.016);
        check(field(player, "animation") == field(player, "walk"), "walking animation");
        player.jump();
        player.update(0.016);
        check(field(player, "animation") == field(player, "jump"), "jump animation");

        player = new Player(assets);
        player.enterLevel(new Level1(assets));
        player.update(0.016);
        Enemy snake = new Enemy(40, 500, 0, 200, 0, assets);
        snake.checkCollision(player);
        snake.checkCollision(player);
        check(player.getHearts() == 3, "snake collision removes only one heart during cooldown");
        boolean seen = false;
        boolean hidden = false;
        for (int i = 0; i < 70; i++) {
            player.update(0.016);
            if (visible(player)) seen = true;
            else hidden = true;
        }
        check(seen && hidden, "player blinks during cooldown");
        for (int i = 0; i < 30; i++) player.update(0.016);
        check(visible(player), "player remains visible after cooldown");
        snake.checkCollision(player);
        check(player.getHearts() == 2, "damage resumes after cooldown");

        int[] first = render(snake);
        snake.update(0.26);
        check(!Arrays.equals(first, render(snake)), "stationary snake visibly animates");
        check(snake.getWidth() == 44 && snake.getHeight() == 30, "collision size preserved");
        BufferedImage frame = ((Animation) field(snake, "animation")).getFrame();
        check(frame.getHeight() > GamePanel.screenY(snake.getHeight()), "snake image enlarged");
        System.out.println("PASS: animation states, timing, snake collision, four hearts, blinking and cooldown.");
    }
}
