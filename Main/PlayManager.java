package Main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import Mino.Block;
import Mino.Mino;
import Mino.Mino_Bar;
import Mino.Mino_L1;
import Mino.Mino_L2;
import Mino.Mino_Square;
import Mino.Mino_T;
import Mino.Mino_Z1;
import Mino.Mino_Z2;

public class PlayManager {
    
    //Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    Mino holdMino;
    final int HOLDMINO_X;
    final int HOLDMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Hold functionality variables
    private boolean canHold = true;

    //Others
    public static int  dropInterval = 60; //mino drops every 60 frames
    boolean gameOver;

    //Effects
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    //Score
    int level = 1;
    int lines;
    int score;

    public PlayManager() {

        //Play Area Frame
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 40;
        NEXTMINO_Y = top_y + 500;

        HOLDMINO_X = right_x + 40;
        HOLDMINO_Y = top_y + 300;

        //SET THE STARTING MINO
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino() {

        Mino mino = null;
        int i = new Random().nextInt(7);

        switch(i) {
            case 0: mino = new Mino_L1();break;
            case 1: mino = new Mino_L2();break;
            case 2: mino = new Mino_Square();break;
            case 3: mino = new Mino_Bar();break;
            case 4: mino = new Mino_T();break;
            case 5: mino = new Mino_Z1();break;
            case 6: mino = new Mino_Z2();break;
        }
        return mino;
    }

    private boolean canMoveLeft(Mino mino) {
        // Check if moving left is possible for each block of the mino
        for(Block block : mino.b) {
            // Check left boundary
            if(block.x - Block.SIZE < left_x) {
                return false;
            }
            
            // Precise collision check with static blocks
            boolean blockBlocked = false;
            for(Block staticBlock : staticBlocks) {
                // Check if there's a block directly to the left
                if(block.x - Block.SIZE == staticBlock.x && block.y == staticBlock.y) {
                    blockBlocked = true;
                    break;
                }
            }
            
            // If this block is blocked, check if the entire mino is blocked
            if(blockBlocked) {
                boolean minoBlocked = true;
                for(Block minoBlock : mino.b) {
                    // Check if any block of the mino can move
                    boolean thisBlockCanMove = true;
                    for(Block staticBlock : staticBlocks) {
                        if(minoBlock.x - Block.SIZE == staticBlock.x && minoBlock.y == staticBlock.y) {
                            thisBlockCanMove = false;
                            break;
                        }
                    }
                    if(thisBlockCanMove) {
                        minoBlocked = false;
                        break;
                    }
                }
                
                // If the entire mino is blocked, return false
                if(minoBlocked) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canMoveRight(Mino mino) {
        // Check if moving right is possible for each block of the mino
        for(Block block : mino.b) {
            // Check right boundary
            if(block.x + Block.SIZE >= right_x) {
                return false;
            }
            
            // Precise collision check with static blocks
            boolean blockBlocked = false;
            for(Block staticBlock : staticBlocks) {
                // Check if there's a block directly to the right
                if(block.x + Block.SIZE == staticBlock.x && block.y == staticBlock.y) {
                    blockBlocked = true;
                    break;
                }
            }
            
            // If this block is blocked, check if the entire mino is blocked
            if(blockBlocked) {
                boolean minoBlocked = true;
                for(Block minoBlock : mino.b) {
                    // Check if any block of the mino can move
                    boolean thisBlockCanMove = true;
                    for(Block staticBlock : staticBlocks) {
                        if(minoBlock.x + Block.SIZE == staticBlock.x && minoBlock.y == staticBlock.y) {
                            thisBlockCanMove = false;
                            break;
                        }
                    }
                    if(thisBlockCanMove) {
                        minoBlocked = false;
                        break;
                    }
                }
                
                // If the entire mino is blocked, return false
                if(minoBlocked) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBottomCollision(Mino mino) {
        mino.checkMovementCollision();
        // Use reflection to access the private field
        try {
            Field bottomCollisionField = Mino.class.getDeclaredField("bottomCollision");
            bottomCollisionField.setAccessible(true);
            return bottomCollisionField.getBoolean(mino);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isMinoPlacementPossible(Mino mino) {
        mino.checkMovementCollision();
        
        try {
            // Use reflection to access private collision fields
            Class<?> minoClass = Mino.class;
            
            // Check bottom collision
            java.lang.reflect.Field bottomCollisionField = minoClass.getDeclaredField("bottomCollision");
            bottomCollisionField.setAccessible(true);
            boolean bottomCollision = bottomCollisionField.getBoolean(mino);
            
            // Check left collision
            java.lang.reflect.Field leftCollisionField = minoClass.getDeclaredField("leftCollision");
            leftCollisionField.setAccessible(true);
            boolean leftCollision = leftCollisionField.getBoolean(mino);
            
            // Check right collision
            java.lang.reflect.Field rightCollisionField = minoClass.getDeclaredField("rightCollision");
            rightCollisionField.setAccessible(true);
            boolean rightCollision = rightCollisionField.getBoolean(mino);
            
            // Placement is impossible if any collision is true
            return !(bottomCollision || leftCollision || rightCollision);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update() {
        // If game is over and space is pressed, reset the game
        if(gameOver && KeyHandler.spacePressed) {
            resetGame();
            KeyHandler.spacePressed = false;
            return;
        }

        // Immediately return if game is over, preventing any further actions
        if(gameOver) {
            return;
        }

        if(KeyHandler.pausePressed) {
            return;
        }

        // Effect counter logic
        if(effectCounterOn) {
            effectCounter++;
            
            // Reset effect after a certain duration
            if(effectCounter >= 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Hold functionality
        if (KeyHandler.shiftJustPressed && canHold) {
            if(holdMino == null) {
                holdMino = currentMino;
                currentMino = nextMino;
                currentMino.setXY(MINO_START_X, MINO_START_Y);
                nextMino = pickMino();
                nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
            } else {
                Mino tempMino = currentMino;
                currentMino = holdMino;
                currentMino.setXY(MINO_START_X, MINO_START_Y);
                holdMino = tempMino;
            }
            canHold = false;
            KeyHandler.shiftJustPressed = false;
        }

        // Rotation logic
        if(KeyHandler.upPressed || KeyHandler.rotateJustPressed) {
            switch(currentMino.direction) {
                case 1: currentMino.getDirection2(); break;
                case 2: currentMino.getDirection3(); break;
                case 3: currentMino.getDirection4(); break;
                case 4: currentMino.getDirection1(); break;
            }
            KeyHandler.upPressed = false;
            KeyHandler.rotateJustPressed = false;
        }

        // Movement logic
        if(KeyHandler.leftPressed) {
            // Check movement collision before moving
            if(canMoveLeft(currentMino)) {
                currentMino.b[0].x -= Block.SIZE;
                currentMino.b[1].x -= Block.SIZE;
                currentMino.b[2].x -= Block.SIZE;
                currentMino.b[3].x -= Block.SIZE;
            }
            KeyHandler.leftPressed = false;
        }

        if(KeyHandler.rightPressed) {
            // Check movement collision before moving
            if(canMoveRight(currentMino)) {
                currentMino.b[0].x += Block.SIZE;
                currentMino.b[1].x += Block.SIZE;
                currentMino.b[2].x += Block.SIZE;
                currentMino.b[3].x += Block.SIZE;
            }
            KeyHandler.rightPressed = false;
        }

        // Hard Drop Logic
        if(KeyHandler.hardDropJustPressed) {
            // Move mino down until it hits bottom or another block
            while(true) {
                // Move blocks down
                currentMino.b[0].y += Block.SIZE;
                currentMino.b[1].y += Block.SIZE;
                currentMino.b[2].y += Block.SIZE;
                currentMino.b[3].y += Block.SIZE;
                
                // Check collision after each move
                currentMino.checkMovementCollision();
                
                // If bottom collision is detected, stop
                if(isBottomCollision(currentMino)) {
                    break;
                }
            }
            
            // Immediately deactivate the mino
            currentMino.active = false;
            
            // Reset hard drop flag
            KeyHandler.hardDropJustPressed = false;
            KeyHandler.hardDropPressed = false;
        }

        // Existing mino placement logic
        if(currentMino.active == false) {
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            checkDelete();

            // Explicit game over check
            for(Block block : currentMino.b) {
                if(block.y < top_y) {
                    gameOver = true;
                    return;
                }
            }

            // Pick new mino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            
            // Check if new mino placement is impossible
            if(!isMinoPlacementPossible(currentMino)) {
                gameOver = true;
                return;
            }
            
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            canHold = true;
        }

        // Existing mino update logic
        currentMino.update();

        if(KeyHandler.downPressed) {
            // Check movement collision before moving
            if(!isBottomCollision(currentMino)) {
                currentMino.b[0].y += Block.SIZE;
                currentMino.b[1].y += Block.SIZE;
                currentMino.b[2].y += Block.SIZE;
                currentMino.b[3].y += Block.SIZE;
            }
            KeyHandler.downPressed = false;
        }
    }

    private void checkDelete() {

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {

            for (int i = 0; i < staticBlocks.size(); i++) {
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if(x == right_x) {

                if (blockCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size()-1; i > -1; i--) {
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;
                    if (lines % 10 == 0 && dropInterval > 1) {

                        level++;
                        if (dropInterval > 10) {
                            dropInterval -= 10;
                        }
                        else {
                            dropInterval -= 1;
                        }
                    }

                    for (int i = 0; i < staticBlocks.size(); i++) {
                        if (staticBlocks.get(i).y < y) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        //Add Score
        if (lineCount > 0) {
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics2D g2) {
        
        //Draw Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        //Effects
        if(effectCounterOn) {
            g2.setColor(Color.white);
            
            for(int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }
        }

        //Draw Mino Frame on the right side
        int x = right_x + 40; 
        int nextBoxY = bottom_y - 200;
        g2.drawRect(x, nextBoxY, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, nextBoxY+60);

        // Draw Hold Frame on the right side
        int holdY = top_y;
        g2.drawRect(x, holdY, 200, 200);
        g2.drawString("HOLD", x+60, holdY+60);

        //Draw Score on the left side
        int scoreX = left_x - 220; 
        g2.drawRect(scoreX, nextBoxY, 200, 200);
        scoreX += 20;
        int y = nextBoxY + 90;
        g2.drawString("LEVEL: " + level, scoreX, y); y += 40;
        g2.drawString("LINES: " + lines, scoreX, y); y += 40;
        g2.drawString("SCORE: " + score, scoreX, y);

        //Draw the NextMino (centered in the box)
        if(nextMino != null) {
            // Calculate center of the NEXT box
            int minoBoxCenterX = x + 100;
            int minoBoxCenterY = nextBoxY + 130; 
            
            // Temporarily adjust mino position for drawing
            int offsetX = minoBoxCenterX - (nextMino.b[0].x + nextMino.b[1].x + nextMino.b[2].x + nextMino.b[3].x) / 4;
            int offsetY = minoBoxCenterY - (nextMino.b[0].y + nextMino.b[1].y + nextMino.b[2].y + nextMino.b[3].y) / 4;
            
            for(Block block : nextMino.b) {
                block.x += offsetX;
                block.y += offsetY;
            }
            
            nextMino.draw(g2);
            
            // Restore original positions
            for(Block block : nextMino.b) {
                block.x -= offsetX;
                block.y -= offsetY;
            }
        }

        //Draw the HoldMino (centered in the box)
        if(holdMino != null) {
            // Calculate center of the HOLD box
            int holdBoxCenterX = x + 100;
            int holdBoxCenterY = holdY + 130; 
            
            // Temporarily adjust mino position for drawing
            int offsetX = holdBoxCenterX - (holdMino.b[0].x + holdMino.b[1].x + holdMino.b[2].x + holdMino.b[3].x) / 4;
            int offsetY = holdBoxCenterY - (holdMino.b[0].y + holdMino.b[1].y + holdMino.b[2].y + holdMino.b[3].y) / 4;
            
            for(Block block : holdMino.b) {
                block.x += offsetX;
                block.y += offsetY;
            }
            
            holdMino.draw(g2);
            
            // Restore original positions
            for(Block block : holdMino.b) {
                block.x -= offsetX;
                block.y -= offsetY;
            }
        }

        //Draw the CurrentMino
        if(currentMino != null) {
            currentMino.draw(g2);
        }

        //Draw static blocks
        for(int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }

        //Game Over and Pause messages
        g2.setColor(Color.red);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        else if(KeyHandler.pausePressed) {
            x = left_x  + 70;
            y = top_y + 320;
            g2.drawString("PAUSED",  x, y);
        }
    }

    public void drawGameOver(Graphics2D g2) {
        if(gameOver) {
            // Semi-transparent dark overlay
            g2.setColor(new Color(0, 0, 0, 200)); 
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
            
            // Game Over Text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            String gameOverText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, (GamePanel.WIDTH - textWidth) / 2, GamePanel.HEIGHT / 2 - 50);
            
            // Score Display
            g2.setFont(new Font("Arial", Font.PLAIN, 30));
            String scoreText = "Score: " + score;
            int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);
            g2.drawString(scoreText, (GamePanel.WIDTH - scoreWidth) / 2, GamePanel.HEIGHT / 2 + 20);
            
            // Restart Hint
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            String restartText = "Press SPACE to Restart";
            int restartWidth = g2.getFontMetrics().stringWidth(restartText);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(restartText, (GamePanel.WIDTH - restartWidth) / 2, GamePanel.HEIGHT / 2 + 100);
        }
    }

    public void resetGame() {
        // Reset all game state variables
        score = 0;
        gameOver = false;
        staticBlocks.clear();
        
        // Reset minos
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
        
        // Reset other game state
        canHold = true;
        holdMino = null;
    }
}
