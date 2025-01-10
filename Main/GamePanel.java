package Main;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable{

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    PlayManager pm;

    public GamePanel() {

        //Panel configuration
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);
        //KeyListener
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);

        pm = new PlayManager();
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // Improved game loop with more precise timing
        final double NANO_PER_SECOND = 1_000_000_000.0;
        final double TARGET_FPS = 60.0;
        final double TIME_BETWEEN_UPDATES = NANO_PER_SECOND / TARGET_FPS;
        
        long lastUpdateTime = System.nanoTime();
        long currentTime;
        long lastRenderTime = System.nanoTime();
        
        while(gameThread != null) {
            currentTime = System.nanoTime();
            
            // Update game state periodically
            if (currentTime - lastUpdateTime >= TIME_BETWEEN_UPDATES) {
                update();
                lastUpdateTime = currentTime;
            }
            
            // Render at target frame rate
            if (currentTime - lastRenderTime >= TIME_BETWEEN_UPDATES) {
                repaint();
                lastRenderTime = currentTime;
            }
            
            // Prevent busy-waiting
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.err.println("Game loop interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void update() {
        if (KeyHandler.pausePressed == false && pm.gameOver == false) {
            pm.update();
        }
    }

    public void paintComponent (Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        pm.draw(g2);
    }
}