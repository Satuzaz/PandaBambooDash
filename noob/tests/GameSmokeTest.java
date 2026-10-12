import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.lang.reflect.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/** Dependency-free checks. Run with assertions enabled; never opens a desktop window. */
public final class GameSmokeTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static Object field(Object object, String name) throws Exception {
        Field f = object.getClass().getDeclaredField(name); f.setAccessible(true); return f.get(object);
    }
    private static void tick(GamePanel panel) throws Exception {
        Method m = GamePanel.class.getDeclaredMethod("update",double.class); m.setAccessible(true); m.invoke(panel,0.016);
    }
    private static void command(GamePanel panel, String command) throws Exception {
        String button = command.equals("menu") ? "menu" : "retry";
        if (field(panel, "state") == GameState.MENU) {
            button = "start";
        }
        java.awt.Rectangle r = UIManager.bounds(button);
        panel.dispatchEvent(new java.awt.event.MouseEvent(panel,
                java.awt.event.MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                0, r.x + 10, r.y + 10, 1, false, java.awt.event.MouseEvent.BUTTON1));
        tick(panel);
    }
    private static void render(GamePanel panel, String file) throws Exception {
        BufferedImage image = new BufferedImage(GamePanel.SCREEN_WIDTH,GamePanel.SCREEN_HEIGHT,BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics(); panel.paint(g); g.dispose();
        ImageIO.write(image,"png",Paths.get("out/check",file+".png").toFile());
    }
    public static void main(String[] args) throws Exception {
        Assets assets = new Assets();
        Level[] levels = {new Level1(assets),new Level2(assets),new Level3(assets)};
        for (Level level : levels) {
            check(level.getItems().size()==5,"five bamboo per level");
            Player p = new Player(assets); p.enterLevel(level); p.update(0.016);
            check(p.isGrounded(),"spawn on ground");
            double y = p.getY(); p.jump(); p.update(0.016); check(p.getY()<y,"jump rises");
            for (int i=0;i<120;i++) p.update(0.016);
            check(p.isGrounded() && Math.abs(p.getY()-482)<0.01,"gravity lands");
            Item item = level.getItems().get(0); item.collect(p); item.collect(p);
            check(p.getBamboo()==1,"collection is idempotent");
            p.takeDamage(); p.takeDamage(); check(p.getHearts()==3,"damage cooldown");
            for (int i=0;i<90;i++) p.update(0.016);
            p.takeDamage(); check(p.getHearts()==2,"damage after cooldown");
            for (Enemy enemy:level.getEnemies()) for(int i=0;i<600;i++) {
                enemy.update(0.016); check(enemy.getX()>=0 && enemy.getX()+enemy.getWidth()<=800,"patrol bounds");
            }
        }
        // Verify an actual jump route from spawn to every bamboo using the game's physics.
        for (Level level : new Level[]{new Level1(assets),new Level2(assets),new Level3(assets)}) {
            Player p = new Player(assets); p.enterLevel(level); p.update(0.016);
            for (Item target : level.getItems()) {
                if (level.getNumber()==2 && target.getY()>450) {
                    // The last item is below the right platform: step off its left side first.
                    p.move(-1); for(int i=0;i<35;i++) p.update(0.016);
                    p.move(0); for(int i=0;i<60;i++) p.update(0.016);
                }
                int frames=0;
                while (!target.isCollected() && frames++<360) {
                    double dx=target.getX()+target.getWidth()/2.0-(p.getX()+p.getWidth()/2.0);
                    p.move(Math.abs(dx)<3?0:dx>0?1:-1);
                    boolean nearEdge = false;
                    for (Platform ground : level.getPlatforms()) {
                        if (Math.abs(p.getY()+p.getHeight()-ground.getY())<0.1 && p.collidesWith(new Platform((int)ground.getX(),(int)ground.getY()-1,ground.getWidth(),2,java.awt.Color.BLACK))) {
                            nearEdge = dx>0 ? ground.getX()+ground.getWidth()-(p.getX()+p.getWidth())<12 : p.getX()-ground.getX()<12;
                        }
                    }
                    if(p.isGrounded() && (target.getY()<p.getY() || level.getNumber()==3) && (Math.abs(dx)<140 || nearEdge)) p.jump();
                    p.update(0.016);
                    for(Item item:level.getItems()) if(!item.isCollected() && item.collidesWith(p)) item.collect(p);
                }
                check(target.isCollected(),"reachable bamboo in level "+level.getNumber()+" at "+target.getX());
                // Settle on its platform before moving to the next target.
                p.move(0); for(int i=0;i<55;i++) p.update(0.016);
            }
        }
        SwingUtilities.invokeAndWait(() -> {
            try {
                GamePanel panel = new GamePanel(); panel.setSize(panel.getPreferredSize());
                check(panel.getWidth()==1280 && panel.getHeight()==720,"screen dimensions");
                check(field(panel,"state")==GameState.MENU,"initial menu"); render(panel,"menu-preview");
                panel.dispatchEvent(new java.awt.event.MouseEvent(panel,java.awt.event.MouseEvent.MOUSE_PRESSED,
                        System.currentTimeMillis(),0,640,455,1,false,java.awt.event.MouseEvent.BUTTON1));
                check(Boolean.TRUE.equals(field(panel,"showingHelp")),"how to play opens");
                check(field(panel,"state")==GameState.MENU,"help keeps menu state");
                render(panel,"help-preview");
                panel.dispatchEvent(new java.awt.event.MouseEvent(panel,java.awt.event.MouseEvent.MOUSE_PRESSED,
                        System.currentTimeMillis(),0,640,495,1,false,java.awt.event.MouseEvent.BUTTON1));
                check(Boolean.FALSE.equals(field(panel,"showingHelp")),"help back closes");
                panel.dispatchEvent(new java.awt.event.MouseEvent(panel,java.awt.event.MouseEvent.MOUSE_PRESSED,
                        System.currentTimeMillis(),0,640,379,1,false,java.awt.event.MouseEvent.BUTTON1));
                tick(panel); check(field(panel,"state")==GameState.PLAYING,"scaled start button");
                render(panel,"level1-preview");
                for(int n=1;n<=3;n++) {
                    Level level=(Level)field(panel,"level"); Player p=(Player)field(panel,"player");
                    check(level.getNumber()==n,"sequential levels");
                    for(Item item:level.getItems()) item.collect(p);
                    tick(panel);
                }
                check(field(panel,"state")==GameState.WIN,"win after level 3"); render(panel,"win-preview");
                command(panel,"menu"); check(field(panel,"state")==GameState.MENU,"return menu");
                command(panel,"primary");
                Player p=(Player)field(panel,"player");
                for(int h=0;h<Player.MAX_HEARTS;h++) { p.takeDamage(); for(int i=0;i<90;i++) tick(panel); }
                check(field(panel,"state")==GameState.GAMEOVER,"zero hearts game over");
                render(panel,"gameover-preview"); command(panel,"primary");
                check(((Player)field(panel,"player")).getHearts()==4,"retry restores hearts");
                check(((Level)field(panel,"level")).getNumber()==1,"retry resets level");
            } catch(Exception e) { throw new RuntimeException(e); }
        });
        System.out.println("PASS: assets, physics, reachable bamboo, patrols, damage, states, retry and rendering.");
    }
}

