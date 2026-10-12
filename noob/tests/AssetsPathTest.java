import javax.swing.SwingUtilities;

/** Run from several working folders to check startup without opening a window. */
public class AssetsPathTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                GamePanel panel = new GamePanel();
                if (panel.getPreferredSize().width != 1280) {
                    throw new AssertionError("Game panel did not initialize");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        System.out.println("PASS: game initialized from " + System.getProperty("user.dir"));
    }
}
