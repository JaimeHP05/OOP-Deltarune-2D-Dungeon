package code.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SusieAnimator {

    private final int STATE_IDLE_WAITING = 0;
    private final int STATE_ANIMATING_SEQUENCE = 1;
    private final int STATE_HOLDING_DRINK = 2;

    private final int FRAME_IDLE = 0;
    private final int FRAME_START = 1;
    private final int FRAME_DRINK = 2;

    private final int FRAME_DUR_MS = 200;
    private final int HOLD_DUR_MS = 1000;
    private final int CHECK_INT_MS = 5000;
    private final int GUARANTEE_INT_MS = 10000;

    private BufferedImage spriteSheet;
    private BufferedImage[] animationFrames;
    private int currentFrame;
    private int currentPhase; 
    private boolean isAnimating;

    private long lastFrameTime;
    private long lastCheckTime;
    private long lastSequenceFinishedTime;
    private Random random;

    public SusieAnimator(String path, int[][] frameCoordinates) {
        this.animationFrames = new BufferedImage[3];
        this.currentFrame = FRAME_IDLE;
        this.currentPhase = STATE_ANIMATING_SEQUENCE;
        this.isAnimating = true;
        this.random = new Random();
        
        loadSpriteSheet(path);
        cutSprites(frameCoordinates);
        
        long currentTime = System.currentTimeMillis();
        this.lastFrameTime = currentTime;
        this.lastCheckTime = currentTime;
        this.lastSequenceFinishedTime = currentTime; 
    }

    private void loadSpriteSheet(String path) {
        try {
            this.spriteSheet = ImageIO.read(new File(path));
        } catch (IOException e) {
            this.spriteSheet = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private void cutSprites(int[][] coords) {
        if (spriteSheet == null) return;
        try {
            for (int i = 0; i < 3; i++) {
                this.animationFrames[i] = spriteSheet.getSubimage(coords[i][0], coords[i][1], coords[i][2], coords[i][3]);
            }
        } catch (Exception e) {
            BufferedImage fallback = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < 3; i++) this.animationFrames[i] = fallback;
        }
    }

    public void update() {
        long now = System.currentTimeMillis();

        if (isAnimating) {
            handleAnimationSequence(now);
        } else {
            handleLoopChecks(now);
        }
    }

    private void handleAnimationSequence(long now) {
        switch (currentPhase) {
            case STATE_IDLE_WAITING:
                if (now - lastFrameTime >= FRAME_DUR_MS) {
                    currentFrame = FRAME_START;
                    lastFrameTime = now;
                    currentPhase = STATE_ANIMATING_SEQUENCE;
                }
                break;

            case STATE_ANIMATING_SEQUENCE:
                if (now - lastFrameTime >= FRAME_DUR_MS) {
                    currentFrame = FRAME_DRINK;
                    lastFrameTime = now;
                    currentPhase = STATE_HOLDING_DRINK;
                }
                break;

            case STATE_HOLDING_DRINK:
                if (now - lastFrameTime >= HOLD_DUR_MS) {
                    currentFrame = FRAME_IDLE;
                    lastFrameTime = now;
                    currentPhase = STATE_IDLE_WAITING;
                    isAnimating = false;
                    lastSequenceFinishedTime = now;
                    lastCheckTime = now; 
                }
                break;
        }
    }

    private void handleLoopChecks(long now) {
        if (now - lastCheckTime >= CHECK_INT_MS) {
            lastCheckTime = now;

            boolean forceTrigger = (now - lastSequenceFinishedTime >= GUARANTEE_INT_MS);

            if (forceTrigger || random.nextBoolean()) {
                isAnimating = true;
                currentFrame = FRAME_IDLE;
                lastFrameTime = now;
                currentPhase = STATE_IDLE_WAITING;
            }
        }
    }

    public BufferedImage getCurrentSprite() {
        if (currentFrame >= 0 && currentFrame < 3 && animationFrames[currentFrame] != null) {
            return animationFrames[currentFrame];
        }
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
}