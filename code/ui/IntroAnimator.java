package code.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IntroAnimator {
    private BufferedImage spriteSheet;
    private BufferedImage[] walkingFrames;
    private BufferedImage idleFrame;

    private static final int[] IDLE_POSE = {24, 0, 19, 38};
    private static final int[] WALK_POSE_1 = {0, 0, 19, 38};
    private static final int[] WALK_POSE_2 = {48, 0, 19, 38};

    public IntroAnimator() {
        walkingFrames = new BufferedImage[3];
        loadSpriteSheet();
        cutSprites();
    }

    private void loadSpriteSheet() {
        try {
            this.spriteSheet = ImageIO.read(new File("code/assets/walk.png"));
            System.out.println("Spritesheet walk.png loaded.");
        } catch (IOException e) {
            System.err.println("Critical error: walk.png not found in code/assets/");
            this.spriteSheet = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private void cutSprites() {
        if (spriteSheet == null) return;
        
        try {
            this.idleFrame = spriteSheet.getSubimage(IDLE_POSE[0], IDLE_POSE[1], IDLE_POSE[2], IDLE_POSE[3]);
            this.walkingFrames[0] = spriteSheet.getSubimage(WALK_POSE_1[0], WALK_POSE_1[1], WALK_POSE_1[2], WALK_POSE_1[3]);
            this.walkingFrames[1] = this.idleFrame; 
            this.walkingFrames[2] = spriteSheet.getSubimage(WALK_POSE_2[0], WALK_POSE_2[1], WALK_POSE_2[2], WALK_POSE_2[3]);
        } catch (Exception e) {
            System.err.println("Error cutting sprites. Check manual coordinates.");
            this.idleFrame = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            this.walkingFrames[0] = this.idleFrame;
            this.walkingFrames[1] = this.idleFrame;
            this.walkingFrames[2] = this.idleFrame;
        }
    }

    public BufferedImage getSprite(int krisX, int controllerX, boolean isPlaying) {
        if (isPlaying) {
            return idleFrame; 
        }
        
        int animationFrame = (Math.abs(krisX) / 10) % 3; 
        return walkingFrames[animationFrame];
    }
}