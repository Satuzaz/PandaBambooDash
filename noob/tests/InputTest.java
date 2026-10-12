import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;


public final class InputTest {
    private static void key(GamePanel panel, String key) {
        String[] parts = key.split(" ");
        int code;
        switch (parts[1]) {
            case "LEFT": code = java.awt.event.KeyEvent.VK_LEFT; break;
            case "RIGHT": code = java.awt.event.KeyEvent.VK_RIGHT; break;
            case "UP": code = java.awt.event.KeyEvent.VK_UP; break;
            case "SPACE": code = java.awt.event.KeyEvent.VK_SPACE; break;
            default: code = parts[1].charAt(0);
        }
        boolean pressed = parts[0].equals("pressed");
        int type = java.awt.event.KeyEvent.KEY_RELEASED;
        if (pressed) type = java.awt.event.KeyEvent.KEY_PRESSED;
        java.awt.event.KeyEvent event = new java.awt.event.KeyEvent(panel, type,
                System.currentTimeMillis(), 0, code, java.awt.event.KeyEvent.CHAR_UNDEFINED);
        if (pressed) panel.keyPressed(event);
        else panel.keyReleased(event);
    }
    private static boolean flag(GamePanel panel, String name) throws Exception {
        Field field = GamePanel.class.getDeclaredField(name); field.setAccessible(true);
        return field.getBoolean(panel);
    }
    private static void clearJump(GamePanel panel) throws Exception {
        Field f = GamePanel.class.getDeclaredField("jumpRequested");
        f.setAccessible(true);
        f.setBoolean(panel, false);
    }
    private static void check(boolean value) { if (!value) throw new AssertionError("Input assertion failed"); }
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                GamePanel p = new GamePanel();
                for (String name : new String[]{"LEFT","A","RIGHT","D"}) {
                    String direction = name.equals("LEFT") || name.equals("A") ? "left" : "right";
                    key(p,"pressed " + name); check(flag(p,direction));
                    key(p,"released " + name); check(!flag(p,direction));
                }
                key(p,"pressed A"); key(p,"pressed LEFT"); key(p,"released A");
                check(flag(p,"left")); key(p,"released LEFT"); check(!flag(p,"left"));
                key(p,"pressed D"); key(p,"pressed RIGHT"); key(p,"released RIGHT");
                check(flag(p,"right")); key(p,"released D");
                for(String name : new String[]{"SPACE","UP","W"}) {
                    key(p,"pressed " + name); check(flag(p,"jumpRequested")); clearJump(p);
                    key(p,"pressed " + name); check(!flag(p,"jumpRequested"));
                    key(p,"released " + name);
                }
                key(p,"pressed W"); clearJump(p);
                key(p,"pressed SPACE"); key(p,"released W");
                check(!flag(p,"jumpRequested") && flag(p,"spaceHeld"));
                key(p,"released SPACE"); key(p,"pressed UP"); check(flag(p,"jumpRequested"));
                Method clear=GamePanel.class.getDeclaredMethod("clearInput"); clear.setAccessible(true); clear.invoke(p);
                check(!flag(p,"jumpRequested") && !flag(p,"spaceHeld"));
                key(p,"pressed W"); check(flag(p,"jumpRequested"));
            } catch(Exception ex) { throw new RuntimeException(ex); }
        });
        System.out.println("PASS: original keys, aliases, overlapping holds, repeat suppression and focus reset.");
    }
}

