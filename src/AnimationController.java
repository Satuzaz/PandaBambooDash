import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** Presentation only: owns frame timing, never changes an entity's geometry or physics. */
public final class AnimationController {
    private static final class Clip {
        private final BufferedImage[] frames;
        private final double frameDuration;
        private final boolean loop;
        private Clip(BufferedImage[] frames, double fps, boolean loop) {
            this.frames = frames.clone(); this.frameDuration = 1.0 / fps; this.loop = loop;
        }
    }
    private final Map<String, Clip> clips = new HashMap<>();
    private String state;
    private int frameIndex;
    private double elapsed;

    public void add(String name, BufferedImage[] frames, double fps, boolean loop) {
        if (name == null || frames == null || frames.length == 0 || !Double.isFinite(fps) || fps <= 0)
            throw new IllegalArgumentException("Animation requires a name, frames and positive FPS");
        for (BufferedImage frame : frames) if (frame == null) throw new IllegalArgumentException("Null frame");
        if (clips.containsKey(name)) throw new IllegalArgumentException("Duplicate animation: " + name);
        clips.put(name, new Clip(frames, fps, loop));
    }
    public void play(String name) {
        if (!clips.containsKey(name)) throw new IllegalArgumentException("Unknown animation: " + name);
        if (!name.equals(state)) { state = name; frameIndex = 0; elapsed = 0; }
    }
    public void update(double dt) {
        if (!Double.isFinite(dt) || dt < 0) throw new IllegalArgumentException("Invalid delta time");
        if (state == null) return;
        Clip clip = clips.get(state);
        elapsed += dt;
        while (elapsed >= clip.frameDuration) {
            elapsed -= clip.frameDuration;
            if (clip.loop) frameIndex = (frameIndex + 1) % clip.frames.length;
            else if (frameIndex < clip.frames.length - 1) frameIndex++;
            else { elapsed = 0; break; }
        }
    }
    public String getState() { return state; }
    public int getFrameIndex() { return frameIndex; }
    public BufferedImage getCurrentFrame() {
        if (state == null) throw new IllegalStateException("No animation selected");
        return clips.get(state).frames[frameIndex];
    }
    public void draw(Graphics2D g, int x, int y, int width, int height, boolean flip) {
        BufferedImage frame = getCurrentFrame();
        // Visual enlargement only, anchored to the original feet; collision stays in Entity.
        int renderWidth = (int)Math.round(width * 1.18);
        int renderHeight = (int)Math.round(height * 1.18);
        x -= (renderWidth-width)/2; y -= renderHeight-height;
        width = renderWidth; height = renderHeight;
        if (flip) g.drawImage(frame,x+width,y,-width,height,null);
        else g.drawImage(frame,x,y,width,height,null);
    }
}
