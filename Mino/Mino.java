package Mino;

import java.awt.Color;
import java.awt.Graphics2D;

import Main.KeyHandler;
import Main.PlayManager;

public class Mino {
    
    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    int autoDropCounter = 0;
    public int direction = 1; //4 Rotations =)
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    // New movement cooldown variables
    private int horizontalMoveCooldown = 0;
    private final int HORIZONTAL_MOVE_DELAY = 5; // Adjust this value to control movement speed
    private int verticalMoveCooldown = 0;
    private final int VERTICAL_MOVE_DELAY = 3; // Adjust this value to control downward movement speed

    // New rotation cooldown variables
    private int rotationCooldown = 0;
    private final int ROTATION_DELAY = 10; // Adjust this value to control rotation speed

    public void create(Color c) {

        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);

        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }

    public void setXY(int x, int y) {}
    public void updateXY(int direction) {

        checkRotationCollision();

        if(leftCollision == false && rightCollision == false && bottomCollision == false) {
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }
    }
    public void getDirection1() {}
    public void getDirection2() {}
    public void getDirection3() {}
    public void getDirection4() {} 
    public void checkMovementCollision() {

        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        checkStaticBlockCollision();

        //Frame Collison
        //Left wall
        for(int i = 0; i < b.length; i++) {
            if(b[i].x == PlayManager.left_x) {
                leftCollision = true;
            }
        }
        //Right wall
        for(int i = 0; i < b.length; i++) {
            if(b[i].x + Block.SIZE == PlayManager.right_x) {
                rightCollision = true;
            }
        }
        //Bottom floor
        for(int i = 0; i < b.length; i++) {
            if(b[i].y +  Block.SIZE == PlayManager.bottom_y) {
                bottomCollision = true;
            }
        }
    }
    public void checkRotationCollision() {

        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        checkStaticBlockCollision();

        for(int i = 0; i < b.length; i++) {
            if(tempB[i].x < PlayManager.left_x) {
                leftCollision = true;
            }
        }
        //Right wall
        for(int i = 0; i < b.length; i++) {
            if(tempB[i].x + Block.SIZE > PlayManager.right_x) {
                rightCollision = true;
            }
        }
        //Bottom floor
        for(int i = 0; i < b.length; i++) {
            if(tempB[i].y +  Block.SIZE > PlayManager.bottom_y) {
                bottomCollision = true;
            }
        }
    }
    private void checkStaticBlockCollision() {

        bottomCollision = false;
        leftCollision = false;
        rightCollision = false;

        for (int i = 0; i < PlayManager.staticBlocks.size(); i++) {
            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;
    
            for (int ii = 0; ii < b.length; ii++) {
                // Check for downward collision
                if (b[ii].y + Block.SIZE == targetY && b[ii].x == targetX) {
                    bottomCollision = true;
                }
    
                // Check for left collision
                if (b[ii].x - Block.SIZE == targetX && b[ii].y == targetY) {
                    leftCollision = true;
                }
    
                // Check for right collision
                if (b[ii].x + Block.SIZE == targetX && b[ii].y == targetY) {
                    rightCollision = true;
                }
            }
        }
    }
    public void update() {

        if(deactivating) {
            deactivating();
        }

        // Rotation logic with single press
        if(KeyHandler.rotateJustPressed && rotationCooldown == 0) {
            switch(direction) {
                case 1: getDirection2();break;
                case 2: getDirection3();break;
                case 3: getDirection4();break;
                case 4: getDirection1();break;
            }
            // Set rotation cooldown after rotation
            rotationCooldown = ROTATION_DELAY;
            // Reset the just pressed flag to prevent multiple rotations
            KeyHandler.rotateJustPressed = false;
        }

        checkMovementCollision();

        // Horizontal movement with cooldown
        if(KeyHandler.leftPressed && horizontalMoveCooldown == 0) {
            if(leftCollision == false) {
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
                horizontalMoveCooldown = HORIZONTAL_MOVE_DELAY;
            }
        }
        if(KeyHandler.rightPressed && horizontalMoveCooldown == 0) {
            if(rightCollision == false) {
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
                horizontalMoveCooldown = HORIZONTAL_MOVE_DELAY;
            }
        }

        // Vertical movement with cooldown
        if(KeyHandler.downPressed && verticalMoveCooldown == 0) {
            if(bottomCollision == false) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
                verticalMoveCooldown = VERTICAL_MOVE_DELAY;
            }
        }

        // Decrement cooldown counters
        if(horizontalMoveCooldown > 0) horizontalMoveCooldown--;
        if(verticalMoveCooldown > 0) verticalMoveCooldown--;
        if(rotationCooldown > 0) rotationCooldown--;

        if(bottomCollision) {
            deactivating = true;
        } else {
            autoDropCounter++; //Increasing Counter every frame
            if(autoDropCounter == PlayManager.dropInterval) {
                //The (sun)mino goes down..
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
            }
        }
    }
    private void deactivating() {

        deactivateCounter++;

        if(deactivateCounter == 45) {

            deactivateCounter = 0;
            checkMovementCollision();

            if(bottomCollision) {
                active = false;
            }
        }
    }

    public void draw(Graphics2D g2) {

        int margin = 2;
        g2.setColor(b[0].c);
        g2.fillRect(b[0].x+margin, b[0].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
        g2.fillRect(b[1].x+margin, b[1].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
        g2.fillRect(b[2].x+margin, b[2].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
        g2.fillRect(b[3].x+margin, b[3].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
    }
}
