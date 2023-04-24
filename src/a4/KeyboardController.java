package a4;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Listens for key events and tracks the state of each keyboard key (up or down).
 */
public class KeyboardController implements KeyListener {

    private static final Map<Integer, Boolean> keyStates = new HashMap<>();

    public KeyboardController() {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyStates.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyStates.put(e.getKeyCode(), false);
    }

    public static boolean isKeyDown(int keyCode) {
        return keyStates.getOrDefault(keyCode, false);
    }

}
