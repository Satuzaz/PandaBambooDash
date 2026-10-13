import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    private Assets assets;
    private UIManager ui;
    private Timer gameTimer;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean jumpHeld;      // ปุ่มกระโดดยังกดค้างอยู่หรือไม่
    private boolean jumpRequested; // กดกระโดดแล้ว รอให้เกมสั่งแพนด้ากระโดด
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
        gameTimer = new Timer(16, this);
    }

    // Timer เรียกเมธอดนี้ทุก 16 มิลลิวินาที
    @Override
    public void actionPerformed(ActionEvent e) {
        update(0.016);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            // ถ้ากดค้างไว้จะไม่กระโดดซ้ำ ต้องปล่อยปุ่มก่อนถึงจะกระโดดใหม่ได้
            if (!jumpHeld) {
                jumpRequested = true;
            }
            jumpHeld = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            jumpHeld = false;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        if (e.getButton() == MouseEvent.BUTTON1) {
            handleClick(e.getPoint());
        }
    }

    // เมธอดที่ interface บังคับให้มี แต่เกมนี้ไม่ได้ใช้งาน
    public void keyTyped(KeyEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    private void clearInput() {
        leftPressed = false;
        rightPressed = false;
        jumpHeld = false;
        jumpRequested = false;
    }

    public void start() {
        if (gameTimer.isRunning()) {
            return;
        }
        requestFocusInWindow();
        gameTimer.start();
    }

    private void handleClick(Point point) {
        if (state == GameState.MENU) {
            if (showingHelp) {
                if (UIManager.MENU_BUTTON.contains(point)) {
                    showingHelp = false;
                }
            } else if (UIManager.START_BUTTON.contains(point)) {
                newGame();
            } else if (UIManager.HELP_BUTTON.contains(point)) {
                showingHelp = true;
            } else if (UIManager.EXIT_BUTTON.contains(point)) {
                System.exit(0);
            }
        } else if (state != GameState.PLAYING) {
            if (UIManager.RETRY_BUTTON.contains(point)) {
                newGame();
            } else if (UIManager.MENU_BUTTON.contains(point)) {
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
        level = new Level(number, assets);
        player.enterLevel(level);
    }

    private void update(double dt) {
        if (state != GameState.PLAYING) {
            jumpRequested = false;
            return;
        }
        int direction = 0;
        if (leftPressed) {
            direction--;
        }
        if (rightPressed) {
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
        } else if (player.getBambooCount() == 5) {
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
