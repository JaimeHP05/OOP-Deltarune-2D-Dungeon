package code.ui;

import code.model.entities.Player;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {
    private BufferedImage spriteSheet;
    
    private Map<String, BufferedImage> idleSprites = new HashMap<>();
    private Map<String, BufferedImage> walkSprites = new HashMap<>();
    private Map<String, BufferedImage> attackSprites = new HashMap<>();
    private BufferedImage defeatedSprite;

    public SpriteLoader() {
        loadSpriteSheet();
        cutSprites();
    }

    private void loadSpriteSheet() {
        try {
            this.spriteSheet = ImageIO.read(new File("code/assets/player_sprites.png"));
        } catch (IOException e) {
            System.err.println("Error: player_sprites.png not found.");
            this.spriteSheet = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private void cutSprites() {
        if (spriteSheet == null) return;
        
        try {
            idleSprites.put("DOWN", spriteSheet.getSubimage(157, 0, 16, 16));
            idleSprites.put("UP", spriteSheet.getSubimage(298, 0, 16, 16));
            idleSprites.put("LEFT", spriteSheet.getSubimage(183, 0, 16, 16));
            idleSprites.put("RIGHT", spriteSheet.getSubimage(230, 0, 16, 16));

            walkSprites.put("DOWN", spriteSheet.getSubimage(135, 0, 16, 16));
            walkSprites.put("UP", spriteSheet.getSubimage(277, 0, 16, 16));
            walkSprites.put("LEFT", spriteSheet.getSubimage(203, 0, 16, 16));
            walkSprites.put("RIGHT", spriteSheet.getSubimage(251, 0, 16, 16));

            attackSprites.put("DOWN", spriteSheet.getSubimage(21, 0, 16, 32)); 
            attackSprites.put("UP", spriteSheet.getSubimage(89, 0, 16, 32));
            attackSprites.put("LEFT", spriteSheet.getSubimage(173, 16, 32, 16));
            attackSprites.put("RIGHT", spriteSheet.getSubimage(289, 16, 32, 16));

            defeatedSprite = spriteSheet.getSubimage(0, 0, 16, 16);

        } catch (Exception e) {
            System.err.println("Error cutting sprites. Check manual coordinates.");
        }
    }

    public BufferedImage getPlayerSprite(Player player) {
        BufferedImage originalSprite = null;
        String dir = player.getFacingDirection().name();

        if (!player.isAlive()) {
            originalSprite = defeatedSprite;
        } else if (player.isAttacking()) {
            originalSprite = attackSprites.get(dir);
        } else if (player.isMoving()) {
            originalSprite = walkSprites.get(dir);
        } else {
            originalSprite = idleSprites.get(dir);
        }

        if (originalSprite == null) originalSprite = spriteSheet.getSubimage(0,0,1,1);

        return applyHealthTints(originalSprite, player);
    }

    private BufferedImage applyHealthTints(BufferedImage sprite, Player player) {
        if (sprite == null) return null;

        BufferedImage tintedSprite = new BufferedImage(sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tintedSprite.createGraphics();
        g2d.drawImage(sprite, 0, 0, null);

        if (!player.isAlive()) {
            applyTint(g2d, tintedSprite.getWidth(), tintedSprite.getHeight(), new Color(255, 0, 0, 180));
        } else if (player.isPoisoned()) {
            applyTint(g2d, tintedSprite.getWidth(), tintedSprite.getHeight(), new Color(148, 0, 211, 130));
        } else if ((double) player.getHp() / player.getMaxHp() <= 0.2) {
            applyTint(g2d, tintedSprite.getWidth(), tintedSprite.getHeight(), new Color(255, 100, 100, 80));
        }

        g2d.dispose();
        return tintedSprite;
    }

    private void applyTint(Graphics2D g2d, int width, int height, Color color) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
    }
}