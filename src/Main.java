import java.io.IOException;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) throws IOException {
        // สร้างหน้าจอเกม แล้วนำไปใส่ในหน้าต่าง
        GamePanel game = new GamePanel();
        JFrame window = new JFrame("Panda Bamboo Dash");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(game);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        game.start();
    }
}
