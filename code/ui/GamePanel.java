package code.ui;

import code.world.Grid;
import code.model.interfaces.Interactable;
import code.model.objects.*;
import code.world.tiles.*;
import code.model.entities.*;

import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import code.model.enums.Direction;

public class GamePanel extends JPanel {
    private Grid grid;
    private Player player;
    private SpriteLoader spriteLoader;

    private int TILE_SIZE = 40;
    private int offsetX = 100;   
    private int offsetY = 10;
    
    private Map<Class<?>, BufferedImage> enemySprites = new HashMap<>();
    private Map<Class<?>, BufferedImage> tileSprites = new HashMap<>();
    private Map<Class<?>, BufferedImage> objectSprites = new HashMap<>();
    
    private BufferedImage imgFloor;
    private BufferedImage imgWallTop;
    private BufferedImage imgWallBottom;
    private BufferedImage imgWallLeft;
    private BufferedImage imgWallRight;
    private BufferedImage imgWallCornerTL; 
    private BufferedImage imgWallCornerTR;
    private BufferedImage imgWallCornerBL; 
    private BufferedImage imgWallCornerBR;
    
    private BufferedImage imgChestClosed;
    private BufferedImage imgChestOpen;
    private BufferedImage imgSavePointSheet;
    private BufferedImage staticBackground;
    private code.world.Room cachedRoom;
    private final int SAVE_POINT_FRAMES = 6;

    public GamePanel(Grid grid, Player player) {
        this.grid = grid;
        this.player = player;
        this.spriteLoader = new SpriteLoader();
        
        loadUIAssets();
        loadDictionaries();
        
        Timer animationTimer = new Timer(150, e -> {
            if (SwingUtilities.getWindowAncestor(this) != null) {
                SwingUtilities.getWindowAncestor(this).repaint();
            }
        });
        animationTimer.start();
    }
    
    private void loadUIAssets() {
        imgFloor = loadImage("code/assets/piso.png");
        imgWallTop = loadImage("code/assets/muro_arriba.png");
        imgWallBottom = loadImage("code/assets/muro_abajo.png");
        imgWallLeft = loadImage("code/assets/muro_izq.png");
        imgWallRight = loadImage("code/assets/muro_der.png");
        imgWallCornerTL = loadImage("code/assets/muro_esq_sup_izq.png");
        imgWallCornerTR = loadImage("code/assets/muro_esq_sup_der.png");
        imgWallCornerBL = loadImage("code/assets/muro_esq_inf_izq.png");
        imgWallCornerBR = loadImage("code/assets/muro_esq_inf_der.png");
        
        imgChestClosed = loadImage("code/assets/cofre_cerrado.png");
        imgChestOpen = loadImage("code/assets/cofre_abierto.png");
        imgSavePointSheet = loadImage("code/assets/save_point.png");
    }

    private void loadDictionaries() {
        enemySprites.put(Goblin.class, loadImage("code/assets/goblin.png"));
        enemySprites.put(Orc.class, loadImage("code/assets/orc.png"));
        enemySprites.put(Snake.class, loadImage("code/assets/snake.png"));
        enemySprites.put(Archer.class, loadImage("code/assets/archer.png"));
        enemySprites.put(Slime.class, loadImage("code/assets/slime.png"));
        enemySprites.put(MiniSlime.class, loadImage("code/assets/mini_slime.png"));
        enemySprites.put(Boss.class, loadImage("code/assets/boss.png"));
        
        tileSprites.put(LavaTile.class, loadImage("code/assets/lava.png"));
        tileSprites.put(HealingTile.class, loadImage("code/assets/healing.png"));
        tileSprites.put(AbyssTile.class, loadImage("code/assets/abyss.png"));
        tileSprites.put(TrampolineTile.class, loadImage("code/assets/trampoline.png"));
        
        objectSprites.put(Trap.class, loadImage("code/assets/trampa.png"));
        objectSprites.put(Stairs.class, loadImage("code/assets/escaleras.png"));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid == null || player == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int width = grid.getWidth();
        int height = grid.getHeight();

        // Not re-rendering background every frame
        if (staticBackground == null || cachedRoom != grid.getCurrentRoom()) {
            cachedRoom = grid.getCurrentRoom();
            buildStaticBackground();
        }

        g2d.drawImage(staticBackground, offsetX, offsetY, null);

        // Interactables and objects
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                code.world.Tile tile = grid.getTile(x, y);
                if (tile == null || !tile.isWalkable()) continue;

                int drawX = offsetX + (x * TILE_SIZE);
                int drawY = offsetY + (y * TILE_SIZE);
                
                Interactable content = tile.getContent();
                if (content != null) {
                    if (content instanceof Chest) {
                        Chest chest = (Chest) content;
                        BufferedImage chestImg = chest.isOpen() ? imgChestOpen : imgChestClosed;
                        if (chestImg != null) g2d.drawImage(chestImg, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                        else { g.setColor(chest.isOpen() ? Color.DARK_GRAY : Color.YELLOW); g.fillRect(drawX + 5, drawY + 5, 30, 30); }
                    } 
                    else if (content instanceof UpgradeStation) {
                        UpgradeStation station = (UpgradeStation) content;
                        if (!station.isUsed() && imgSavePointSheet != null) {
                            int currentFrame = (int) ((System.currentTimeMillis() / 150) % SAVE_POINT_FRAMES);
                            int frameWidth = imgSavePointSheet.getWidth() / SAVE_POINT_FRAMES;
                            int frameHeight = imgSavePointSheet.getHeight();
                            g2d.drawImage(imgSavePointSheet, drawX, drawY, drawX + TILE_SIZE, drawY + TILE_SIZE, 
                                currentFrame * frameWidth, 0, (currentFrame + 1) * frameWidth, frameHeight, null);
                        }
                    } else {
                        BufferedImage objImg = objectSprites.get(content.getClass());
                        if (objImg != null) g2d.drawImage(objImg, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                    }
                }
            }
        }
        // Enemies
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Enemy enemy = grid.getEnemyAt(x, y);
                if (enemy != null) {
                    int enemyDrawX = offsetX + (enemy.getX() * TILE_SIZE);
                    int enemyDrawY = offsetY + (y * TILE_SIZE); 
                    
                    BufferedImage enemySprite = enemySprites.get(enemy.getClass());
                    
                    if (enemySprite != null) {
                        g2d.drawImage(enemySprite, enemyDrawX, enemyDrawY, TILE_SIZE, TILE_SIZE, null);
                    } else {
                        g.setColor(Color.RED);
                        g.fillRect(enemyDrawX + 5, enemyDrawY + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                    }
                }
            }
        }
        // Player
        BufferedImage krisSprite = spriteLoader.getPlayerSprite(player);
        if (krisSprite != null) {
            int scale = 2;
            int drawW = krisSprite.getWidth() * scale;
            int drawH = krisSprite.getHeight() * scale;
            int px = offsetX + (player.getX() * TILE_SIZE) + (TILE_SIZE - drawW) / 2;
            int py = offsetY + (player.getY() * TILE_SIZE) + (TILE_SIZE - drawH) / 2;
            
            if (player.isAttacking()) {
                Direction dir = player.getFacingDirection();
                // For cool moving feeling
                if (dir == Direction.RIGHT) px += 20; 
                else if (dir == Direction.LEFT) px -= 20; 
                else if (dir == Direction.UP) py -= 20;
                else if (dir == Direction.DOWN) py += 20; 
            }
            g2d.drawImage(krisSprite, px, py, drawW, drawH, null);
        }
        // Room rules (poison gas and such)
        code.world.Room currentRoom = grid.getCurrentRoom();
        if (currentRoom != null && currentRoom.getRules() != null && !currentRoom.getRules().isEmpty()) {
            g2d.setColor(new Color(150, 0, 200, 40)); 
            g2d.fillRect(offsetX, offsetY, width * TILE_SIZE, height * TILE_SIZE);
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Warning: Missing sprite: " + path);
            return null;
        }
    }

    // Background that only changes when entering new room
    private void buildStaticBackground() {
        int width = grid.getWidth();
        int height = grid.getHeight();
        staticBackground = new BufferedImage(width * TILE_SIZE, height * TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = staticBackground.createGraphics();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                code.world.Tile tile = grid.getTile(x, y);
                if (tile == null) continue;

                int drawX = x * TILE_SIZE; 
                int drawY = y * TILE_SIZE;

                if (tile.isWalkable()) {
                    if (imgFloor != null) g2d.drawImage(imgFloor, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                    else { g2d.setColor(new Color(220, 220, 220)); g2d.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE); }
    
                    BufferedImage tileImg = tileSprites.get(tile.getClass());
                    if (tileImg != null) {
                        g2d.drawImage(tileImg, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                    }
                } else {
                    BufferedImage tileImg = tileSprites.get(tile.getClass());
                    if (tileImg != null) {
                        g2d.drawImage(tileImg, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                    } else {
                        BufferedImage wallImg = null;
                        if (x == 0 && y == 0) wallImg = imgWallCornerTL;
                        else if (x == width - 1 && y == 0) wallImg = imgWallCornerTR;
                        else if (x == 0 && y == height - 1) wallImg = imgWallCornerBL;
                        else if (x == width - 1 && y == height - 1) wallImg = imgWallCornerBR;
                        else if (y == 0) wallImg = imgWallTop;
                        else if (y == height - 1) wallImg = imgWallBottom;
                        else if (x == 0) wallImg = imgWallLeft;
                        else if (x == width - 1) wallImg = imgWallRight;
                        else wallImg = imgWallTop;

                        if (wallImg != null) g2d.drawImage(wallImg, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                        else { g2d.setColor(Color.DARK_GRAY); g2d.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE); }
                    }
                }
            }
        }
        g2d.dispose();
    }
}