import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class GamePanel extends JPanel implements Runnable {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    // Keep level geometry and physics in the original logical coordinate space.
    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 600;
    private final Assets assets;
    private final UIManager ui;
    private final Object modelLock = new Object();
    private final AtomicBoolean left = new AtomicBoolean(), right = new AtomicBoolean();
    private final AtomicBoolean spaceHeld = new AtomicBoolean(), jumpRequested = new AtomicBoolean();
    private int heldKeys;
    private final AtomicReference<String> command = new AtomicReference<>();
    private volatile boolean running;
    private Thread loop;
    private GameState state = GameState.MENU;
    private Player player;
    private Level level;
    private final Rectangle primaryButton = UIManager.bounds("start");
    private final Rectangle howButton = UIManager.bounds("help");
    private final Rectangle exitButton = UIManager.bounds("exit");
    private final Rectangle retryButton = UIManager.bounds("retry");
    private final Rectangle menuButton = UIManager.bounds("menu");
    private boolean showingHelp;
    private Point hover = new Point(-1,-1);

    public GamePanel() throws Exception {
        assets = new Assets();
        ui = new UIManager(assets);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setDoubleBuffered(true);
        String[] keys = {"LEFT", "A", "RIGHT", "D", "SPACE", "UP", "W"};
        for (int i = 0; i < keys.length; i++) {
            final int bit = 1 << i;
            bind("pressed " + keys[i], () -> setKeyHeld(bit, true));
            bind("released " + keys[i], () -> setKeyHeld(bit, false));
        }
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                double x = e.getX() * (double) SCREEN_WIDTH / getWidth();
                double y = e.getY() * (double) SCREEN_HEIGHT / getHeight();
                synchronized (modelLock) {
                    if (state == GameState.MENU) {
                        if (showingHelp) { if (menuButton.contains(x,y)) showingHelp = false; }
                        else if (primaryButton.contains(x,y)) command.set("primary");
                        else if (howButton.contains(x,y)) showingHelp = true;
                        else if (exitButton.contains(x,y)) {
                            Window window = SwingUtilities.getWindowAncestor(GamePanel.this);
                            if (window != null) window.dispatchEvent(new WindowEvent(window,WindowEvent.WINDOW_CLOSING));
                        }
                    } else if (state != GameState.PLAYING) {
                        if (retryButton.contains(x,y)) command.set("primary");
                        else if (menuButton.contains(x,y)) command.set("menu");
                    }
                }
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                hover = new Point(e.getX()*SCREEN_WIDTH/getWidth(),e.getY()*SCREEN_HEIGHT/getHeight());
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { hover = new Point(-1,-1); repaint(); }
        });
    }
    private void bind(String key, Runnable action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),key);
        getActionMap().put(key,new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }
    public void start() {
        if (running) return;
        running = true;
        SwingUtilities.getWindowAncestor(this).addWindowFocusListener(new WindowAdapter() {
            @Override public void windowLostFocus(WindowEvent e) { clearInput(); }
        });
        loop = new Thread(this,"Panda-Game-Loop");
        loop.setDaemon(true); loop.start();
    }
    private synchronized void setKeyHeld(int bit, boolean pressed) {
        heldKeys = pressed ? heldKeys | bit : heldKeys & ~bit;
        left.set((heldKeys & 3) != 0);
        right.set((heldKeys & 12) != 0);
        boolean jumping = (heldKeys & 112) != 0;
        if (jumping && !spaceHeld.get()) jumpRequested.set(true);
        spaceHeld.set(jumping);
    }
    private synchronized void clearInput() {
        heldKeys = 0;
        left.set(false); right.set(false); spaceHeld.set(false); jumpRequested.set(false);
    }
    @Override public void removeNotify() { running = false; if (loop != null) loop.interrupt(); super.removeNotify(); }
    private void newGame() {
        clearInput(); player = new Player(assets); loadLevel(1); state = GameState.PLAYING;
    }
    private void loadLevel(int number) {
        switch (number) {
            case 1: level = new Level1(assets); break;
            case 2: level = new Level2(assets); break;
            case 3: level = new Level3(assets); break;
            default: throw new IllegalArgumentException("Invalid level");
        }
        player.enterLevel(level);
    }
    private void update(double dt) {
        String action = command.getAndSet(null);
        if (state != GameState.PLAYING) {
            if ("primary".equals(action)) newGame();
            else if ("menu".equals(action) && state != GameState.MENU) { state = GameState.MENU; clearInput(); }
            jumpRequested.set(false);
            return;
        }
        player.move((right.get() ? 1 : 0) - (left.get() ? 1 : 0));
        if (jumpRequested.getAndSet(false)) player.jump();
        player.update(dt);
        level.update(dt,player);
        if (player.getHearts() == 0) { state = GameState.GAMEOVER; clearInput(); }
        else if (player.getBamboo() == 5) {
            if (level.getNumber() == 3) { state = GameState.WIN; clearInput(); }
            else { loadLevel(level.getNumber()+1); jumpRequested.set(false); }
        }
    }
    @Override public void run() {
        final double step = 1.0 / 60.0;
        long previous = System.nanoTime();
        double accumulated = 0;
        while (running) {
            long now = System.nanoTime();
            accumulated += Math.min((now-previous)/1_000_000_000.0,0.1); previous = now;
            synchronized (modelLock) {
                while (accumulated >= step) { update(step); accumulated -= step; }
            }
            repaint();
            try { Thread.sleep(Math.max(1,(long)((step-accumulated)*1000))); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
    }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D)graphics.create();
        g.scale(getWidth() / (double) SCREEN_WIDTH, getHeight() / (double) SCREEN_HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        synchronized (modelLock) {
            if (state == GameState.PLAYING) {
                renderWorld(g); ui.renderHUD(g,player,level);
            } else {
                if (state == GameState.MENU) ui.renderMainMenu(g,hover,showingHelp);
                else if (state == GameState.WIN) { renderWorld(g); ui.renderWin(g,hover); }
                else { renderWorld(g); ui.renderGameOver(g,hover); }
            }
        }
        g.dispose();
    }
    private void renderWorld(Graphics2D g) {
        Graphics2D world = (Graphics2D)g.create();
        world.scale(SCREEN_WIDTH/(double)WORLD_WIDTH,SCREEN_HEIGHT/(double)WORLD_HEIGHT);
        level.draw(world); player.draw(world); world.dispose();
    }
}
