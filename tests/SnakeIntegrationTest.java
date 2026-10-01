import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/** Runs the same update path used by GamePanel.run(), then compares actual draw output. */
public final class SnakeIntegrationTest {
    private static Object field(Object target,String name) throws Exception {
        Field field=target.getClass().getDeclaredField(name); field.setAccessible(true); return field.get(target);
    }
    private static BufferedImage render(Enemy snake) {
        BufferedImage image=new BufferedImage(96,80,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=image.createGraphics();
        // Remove world translation so walking alone cannot make this test pass.
        g.translate(24-snake.getX(),24-snake.getY());
        snake.draw(g); g.dispose(); return image;
    }
    private static void check(boolean condition,String message) {
        if(!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                GamePanel game=new GamePanel();
                Method newGame=GamePanel.class.getDeclaredMethod("newGame"); newGame.setAccessible(true); newGame.invoke(game);
                Method update=GamePanel.class.getDeclaredMethod("update",double.class); update.setAccessible(true);
                Level level=(Level)field(game,"level");
                Enemy snake=level.getEnemies().get(0);
                AnimationController animation=(AnimationController)field(snake,"animation");
                int firstIndex=animation.getFrameIndex();
                BufferedImage first=render(snake);
                double initialX=snake.getX();
                for(int i=0;i<21;i++) update.invoke(game,1.0/60.0);
                check(animation.getFrameIndex()!=firstIndex,"Game loop path must advance snake frame");
                BufferedImage second=render(snake);
                int[] a=first.getRGB(0,0,96,80,null,0,96),b=second.getRGB(0,0,96,80,null,0,96);
                int different=0; for(int i=0;i<a.length;i++) if(a[i]!=b[i]) different++;
                check(different>50,"Snake.draw must change pose independently of walking");
                check(snake.getX()>initialX,"Original patrol still moves");
                check(snake.getWidth()==44 && snake.getHeight()==30,"Collision dimensions unchanged");
                for(int i=0;i<60;i++) update.invoke(game,1.0/60.0);
                check(animation.getFrameIndex()==firstIndex,"Animation must loop back");
                check(Arrays.equals(a,render(snake).getRGB(0,0,96,80,null,0,96)),"Same frame must render the same pose");
                BufferedImage proof=new BufferedImage(384,160,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g=proof.createGraphics();
                g.setColor(new java.awt.Color(236,246,213)); g.fillRect(0,0,384,160);
                g.drawImage(first,0,0,192,160,null); g.drawImage(second,192,0,192,160,null); g.dispose();
                ImageIO.write(proof,"png",Paths.get("out","snake-animation-proof.png").toFile());
                System.out.println("PASS: GamePanel.update -> Level.update -> Enemy.update -> animation.update; Enemy.draw changed "+different+" pixels after removing movement; frame loops correctly.");
            } catch(Exception e) { throw new RuntimeException(e); }
        });
    }
}
