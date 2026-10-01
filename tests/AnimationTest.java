import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

public final class AnimationTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static AnimationController controller(Object object) throws Exception {
        Field field = object.getClass().getDeclaredField("animation"); field.setAccessible(true);
        return (AnimationController)field.get(object);
    }
    public static void main(String[] args) throws Exception {
        BufferedImage a = new BufferedImage(2,2,BufferedImage.TYPE_INT_ARGB);
        BufferedImage b = new BufferedImage(2,2,BufferedImage.TYPE_INT_ARGB);
        AnimationController animation = new AnimationController();
        animation.add("loop",new BufferedImage[]{a,b},10,true);
        animation.add("once",new BufferedImage[]{a,b},10,false);
        animation.play("loop"); animation.update(0.11);
        check(animation.getCurrentFrame()==b,"advances with elapsed time");
        animation.play("loop"); check(animation.getCurrentFrame()==b,"same state does not restart");
        animation.update(0.11); check(animation.getCurrentFrame()==a,"loops");
        animation.play("once"); check(animation.getFrameIndex()==0,"transition resets");
        animation.update(2); check(animation.getCurrentFrame()==b,"non-loop holds last frame");
        Assets assets = new Assets();
        for(String path:new String[]{"player/animation/idle.png","player/animation/walk.png",
                "player/animation/jump.png","enemy/animation/idle.png","enemy/animation/move.png"}) {
            BufferedImage[] frames=assets.frames(path,4);
            check(frames.length==4,"four frames");
            check(assets.get(path).getColorModel().hasAlpha(),"transparent sprite sheet");
            check((assets.get(path).getRGB(0,0) >>> 24)==0,"background is truly transparent");
        }
        Player player=new Player(assets); player.enterLevel(new Level1(assets)); player.update(1.0/60);
        check(controller(player).getState().equals("idle"),"standing idle");
        player.move(1); player.update(1.0/60);
        check(controller(player).getState().equals("walk"),"walking");
        player.jump(); player.update(1.0/60);
        check(controller(player).getState().equals("jump"),"airborne jump");
        player.move(0); for(int i=0;i<120;i++) player.update(1.0/60);
        check(controller(player).getState().equals("idle"),"landing returns to idle");
        Enemy moving=new Enemy(100,500,80,250,75,assets);
        Enemy idle=new Enemy(100,500,80,250,0,assets);
        for (String type : new String[]{"idle","move"}) {
            BufferedImage first=assets.get("snake_"+type+"_1"), second=assets.get("snake_"+type+"_2");
            boolean different=false;
            for(int y=0;y<Math.min(first.getHeight(),second.getHeight()) && !different;y++) {
                for(int x=0;x<Math.min(first.getWidth(),second.getWidth());x++) {
                    if(first.getRGB(x,y)!=second.getRGB(x,y)) { different=true; break; }
                }
            }
            check(different,"snake "+type+" frames contain different artwork");
        }
        BufferedImage initialIdle=controller(idle).getCurrentFrame();
        idle.update(0.51);
        check(controller(idle).getCurrentFrame()!=initialIdle && idle.getX()==100,"idle pose animates without movement");
        BufferedImage initialMove=controller(moving).getCurrentFrame();
        moving.update(0.34);
        check(controller(moving).getCurrentFrame()!=initialMove,"moving snake cycles frame");
        moving=new Enemy(100,500,80,250,75,assets);
        moving.update(0.1); idle.update(0.1);
        check(controller(moving).getState().equals("move"),"moving snake");
        check(controller(idle).getState().equals("idle"),"stationary snake");
        check(moving.getX()==105.625 && idle.getX()==100,"enemy movement reduced by 25 percent");
        java.awt.geom.Rectangle2D before=player.getBounds();
        BufferedImage canvas=new BufferedImage(800,600,BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g=canvas.createGraphics(); player.draw(g); moving.draw(g); g.dispose();
        check(before.equals(player.getBounds()) && player.getWidth()==40 && player.getHeight()==48,"drawing preserves player collision");
        check(moving.getWidth()==44 && moving.getHeight()==30,"enemy collision size unchanged");
        System.out.println("PASS: animation timing, looping, states, sheets and unchanged collision dimensions.");
    }
}
