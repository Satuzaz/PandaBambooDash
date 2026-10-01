import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class InputTest {
    private static void key(GamePanel panel, String key) {
        Object binding = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke(key));
        if (binding == null) throw new AssertionError("Missing binding: " + key);
        panel.getActionMap().get(binding).actionPerformed(new ActionEvent(panel,0,key));
    }
    private static AtomicBoolean flag(GamePanel panel, String name) throws Exception {
        Field field = GamePanel.class.getDeclaredField(name); field.setAccessible(true);
        return (AtomicBoolean)field.get(panel);
    }
    private static void check(boolean value) { if (!value) throw new AssertionError("Input assertion failed"); }
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                GamePanel p = new GamePanel();
                for (String name : new String[]{"LEFT","A","RIGHT","D"}) {
                    String direction = name.equals("LEFT") || name.equals("A") ? "left" : "right";
                    key(p,"pressed " + name); check(flag(p,direction).get());
                    key(p,"released " + name); check(!flag(p,direction).get());
                }
                key(p,"pressed A"); key(p,"pressed LEFT"); key(p,"released A");
                check(flag(p,"left").get()); key(p,"released LEFT"); check(!flag(p,"left").get());
                key(p,"pressed D"); key(p,"pressed RIGHT"); key(p,"released RIGHT");
                check(flag(p,"right").get()); key(p,"released D");
                for(String name : new String[]{"SPACE","UP","W"}) {
                    key(p,"pressed " + name); check(flag(p,"jumpRequested").getAndSet(false));
                    key(p,"pressed " + name); check(!flag(p,"jumpRequested").get());
                    key(p,"released " + name);
                }
                key(p,"pressed W"); flag(p,"jumpRequested").set(false);
                key(p,"pressed SPACE"); key(p,"released W");
                check(!flag(p,"jumpRequested").get() && flag(p,"spaceHeld").get());
                key(p,"released SPACE"); key(p,"pressed UP"); check(flag(p,"jumpRequested").get());
                Method clear=GamePanel.class.getDeclaredMethod("clearInput"); clear.setAccessible(true); clear.invoke(p);
                check(!flag(p,"jumpRequested").get() && !flag(p,"spaceHeld").get());
                key(p,"pressed W"); check(flag(p,"jumpRequested").get());
            } catch(Exception ex) { throw new RuntimeException(ex); }
        });
        System.out.println("PASS: original keys, aliases, overlapping holds, repeat suppression and focus reset.");
    }
}
