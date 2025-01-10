package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    public static boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed, rotatePressed, rotateJustPressed;
    public static boolean shiftPressed, shiftJustPressed;
    public static boolean hardDropPressed, hardDropJustPressed;
    public static boolean spacePressed = false;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (!rotatePressed) {
                rotateJustPressed = true;
            }
            rotatePressed = true;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            pausePressed = !pausePressed;
        }
        if (code == KeyEvent.VK_SHIFT) {
            if (!shiftPressed) {
                shiftJustPressed = true;
            }
            shiftPressed = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            // Distinguish between hard drop and game restart
            if(!hardDropPressed) {
                hardDropPressed = true;
                hardDropJustPressed = true;
            }
            spacePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            rotatePressed = false;
            rotateJustPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = false;
            shiftJustPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            hardDropPressed = false;
            hardDropJustPressed = false;
            spacePressed = false;
        }
    }
    
}
