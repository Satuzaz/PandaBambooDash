import javax.swing.*;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GamePanel game = new GamePanel();
                JFrame window = new JFrame("Panda Bamboo Dash");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setResizable(false);
                window.setContentPane(game);
                window.pack();
                window.setLocationRelativeTo(null);
                window.setVisible(true);
                game.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Cannot start game", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
