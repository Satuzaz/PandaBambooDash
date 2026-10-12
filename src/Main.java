import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // ให้ Swing เป็นผู้สร้างและดูแลหน้าต่างเกม
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                openGame();
            }
        });
    }

    private static void openGame() {
        try {
            GamePanel game = new GamePanel();
            JFrame window = new JFrame("Panda Bamboo Dash");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.add(game);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            game.start();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot start game", JOptionPane.ERROR_MESSAGE);
        }
    }
}
