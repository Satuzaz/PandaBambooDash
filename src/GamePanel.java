import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, FocusListener {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    private Assets assets;
    private UIManager ui;
    private Timer timer;
    private boolean[] keys = new boolean[256];
    private boolean left;
    private boolean right;
    private boolean spaceHeld;
    private boolean jumpRequested;
    private GameState state = GameState.MENU;
    private Player player;
    private Level level;
    private boolean showingHelp;


    // ตำแหน่งเกมใช้ 800 x 600 แปลงเป็นตำแหน่งหน้าจอเฉพาะตอนวาด
    public static int screenX(double x) {
        return (int) Math.round(x * SCREEN_WIDTH / 800.0);
    }

    public static int screenY(double y) {
        return (int) Math.round(y * SCREEN_HEIGHT / 600.0);
    }

    public GamePanel() throws IOException {
        assets = new Assets();
        ui = new UIManager(assets);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addFocusListener(this);
        timer = new Timer(16, this);
    }

    // Timer เรียกเมธอดนี้ทุก 16 มิลลิวินาที
    @Override
    public void actionPerformed(ActionEvent e) {
        update(0.016);
        repaint();
    }

    // ใช้รหัสปุ่มเป็นช่องในอาร์เรย์ true = กดอยู่, false = ปล่อยแล้ว
    private void setKey(int code, boolean pressed) {
        if (code < 0 || code >= keys.length) {
            return;
        }
        keys[code] = pressed;
        left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
        boolean jumping = keys[KeyEvent.VK_SPACE] || keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
        if (jumping && !spaceHeld) {
            jumpRequested = true;
        }
        spaceHeld = jumping;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        setKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKey(e.getKeyCode(), false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        if (e.getButton() == MouseEvent.BUTTON1) {
            handleClick(e.getPoint());
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        clearInput();
    }

    // เมธอดที่ interface บังคับให้มี แต่เกมนี้ไม่ได้ใช้งาน
    public void keyTyped(KeyEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void focusGained(FocusEvent e) { }

    private void clearInput() {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = false;
        }
        left = false;
        right = false;
        spaceHeld = false;
        jumpRequested = false;
    }

    public void start() {
        if (timer.isRunning()) {
            return;
        }
        requestFocusInWindow();
        timer.start();
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    private void handleClick(Point point) {
        if (state == GameState.MENU) {
            if (showingHelp) {
                if (UIManager.bounds("menu").contains(point)) {
                    showingHelp = false;
                }
            } else if (UIManager.bounds("start").contains(point)) {
                newGame();
            } else if (UIManager.bounds("help").contains(point)) {
                showingHelp = true;
            } else if (UIManager.bounds("exit").contains(point)) {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                }
            }
        } else if (state != GameState.PLAYING) {
            if (UIManager.bounds("retry").contains(point)) {
                newGame();
            } else if (UIManager.bounds("menu").contains(point)) {
                state = GameState.MENU;
                clearInput();
            }
        }
        repaint();
    }

    private void newGame() {
        clearInput();
        player = new Player(assets);
        loadLevel(1);
        state = GameState.PLAYING;
    }

    private void loadLevel(int number) {
        if (number == 1) {
            level = new Level1(assets);
        } else if (number == 2) {
            level = new Level2(assets);
        } else {
            level = new Level3(assets);
        }
        player.enterLevel(level);
    }

    private void update(double dt) {
        if (state != GameState.PLAYING) {
            jumpRequested = false;
            return;
        }
        int direction = 0;
        if (left) {
            direction--;
        }
        if (right) {
            direction++;
        }
        player.move(direction);
        if (jumpRequested) {
            player.jump();
            jumpRequested = false;
        }
        player.update(dt);
        level.update(dt, player);
        if (player.getHearts() == 0) {
            state = GameState.GAMEOVER;
            clearInput();
        } else if (player.getBamboo() == 5) {
            if (level.getNumber() == 3) {
                state = GameState.WIN;
                clearInput();
            } else {
                loadLevel(level.getNumber() + 1);
                jumpRequested = false;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == GameState.MENU) {
            ui.renderMainMenu(g, showingHelp);
            return;
        }
        level.draw(g);
        player.draw(g);
        if (state == GameState.PLAYING) {
            ui.renderHUD(g, player, level);
        } else if (state == GameState.WIN) {
            ui.renderWin(g);
        } else {
            ui.renderGameOver(g);
        }
    }
}
