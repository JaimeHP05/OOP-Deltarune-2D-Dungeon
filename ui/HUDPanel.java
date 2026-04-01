package code.ui;

import code.model.entities.Player;
import code.model.objects.Item;
import code.world.Grid;
import code.world.Room;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HUDPanel extends JPanel {
    private Player player;
    private Grid grid;
    private String systemStatus = "PRESS SPACE TO SKIP"; 
    
    private int marginX = 25;  
    private BufferedImage backgroundImage;

    public HUDPanel(Player player) {
        this.player = player;
        this.setPreferredSize(new Dimension(300, 700));
        try {
            this.backgroundImage = ImageIO.read(new File("code/assets/hud_bg.png"));
        } catch (IOException e) {
            this.setBackground(new Color(20, 20, 20));
        }
    }

    public void setPlayer(Player player) { this.player = player; this.repaint(); }
    public void setGrid(Grid grid) { this.grid = grid; this.repaint(); }
    public void setSystemStatus(String status) { this.systemStatus = status; this.repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        int safeBottomY = 630;
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.YELLOW);
        if (systemStatus != null && !systemStatus.trim().isEmpty()) {
            g.drawString(systemStatus, marginX, safeBottomY);
        }

        if (player == null) return;
        
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.MAGENTA);
        g.drawString("Dungeon: Level " + player.getCurrentLevel(), marginX, 35);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("HP: " + player.getHp() + " / " + player.getMaxHp(), marginX, 65);

        g.setColor(Color.ORANGE);
        g.drawString("Hunger: " + player.getHunger() + "%", marginX, 85);

        String statusText = player.getStatusText();
        
        if (statusText.equals("DEAD!")) g.setColor(Color.RED);
        else if (statusText.equals("Poisoned")) g.setColor(Color.MAGENTA);
        else g.setColor(Color.GREEN);
        
        g.drawString("Status: " + statusText, marginX, 105);
        
        g.setFont(new Font("Arial", Font.BOLD, 15));
        if (grid != null && grid.getCurrentRoom() != null) {
            Room currentRoom = grid.getCurrentRoom();
            if (currentRoom.getRules() != null && !currentRoom.getRules().isEmpty()) {
                g.setColor(Color.MAGENTA);
                String shortRule = currentRoom.getRules().get(0).getDescription().split(":")[0];
                g.drawString("[!] ZONE: " + shortRule.toUpperCase(), marginX, 135);
            } else {
                g.setColor(Color.CYAN);
                g.drawString("Zone: Safe", marginX, 135);
            }
        } else {
            g.setColor(Color.CYAN);
            g.drawString("Zone: Unknown", marginX, 135);
        }
        
        g.setColor(Color.WHITE);
        g.drawString("Total Damage: " + player.getAttackDamage(), marginX, 165);
        
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        int passiveY = 185;
        boolean hasBuffs = false;
        
        if (player.getVampirismLevel() > 0) { g.setColor(Color.RED); g.drawString("+" + (player.getVampirismLevel() * 2) + " HP per attack", marginX + 10, passiveY); passiveY += 20; hasBuffs = true; }
        if (player.getTrapImmunityLevel() > 0) { g.setColor(Color.CYAN); g.drawString("Trap Immunity", marginX + 10, passiveY); passiveY += 20; hasBuffs = true; }
        if (player.getFireLevel() > 0) { g.setColor(new Color(255, 100, 0)); g.drawString("+" + (player.getFireLevel() * 10) + " Fire Damage", marginX + 10, passiveY); passiveY += 20; hasBuffs = true; }
        if (player.getElectricLevel() > 0) { g.setColor(Color.MAGENTA); g.drawString("Roots target (" + player.getElectricLevel() + " turn)", marginX + 10, passiveY); passiveY += 20; hasBuffs = true; }

        if (!hasBuffs) {
            g.setColor(Color.GRAY);
            g.drawString("- No buffs active", marginX + 10, passiveY);
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("Inventory:", marginX, 270); 
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        
        List<Item> items = player.getInventoryItems();
        for (int i = 0; i < 3; i++) {
            int yPos = 290 + (i * 20);
            if (i < items.size()) {
                g.setColor(Color.YELLOW);
                g.drawString((i + 1) + ": " + items.get(i).getName(), marginX + 10, yPos);
            } else {
                g.setColor(Color.GRAY);
                g.drawString((i + 1) + ": [Empty]", marginX + 10, yPos);
            }
        }

        if (grid != null) {
            Room currentRoom = grid.getCurrentRoom();
            
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(Color.WHITE);
            g.drawString("Map:", marginX, 350); 

            Room[][] map = grid.getDungeonMap();
            
            int mapStartX = marginX + 10;
            int mapStartY = 365; 
            int roomSize = 20; 
            int padding = 4;
            
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    Room r = map[x][y];
                    if (r != null) {
                        int drawX = mapStartX + x * (roomSize + padding);
                        int drawY = mapStartY + y * (roomSize + padding);

                        if (r == currentRoom) g.setColor(Color.WHITE);
                        else if (r.isExplored()) g.setColor(Color.DARK_GRAY);
                        else g.setColor(Color.LIGHT_GRAY);
                        
                        g.fillRect(drawX, drawY, roomSize, roomSize);

                        if (r == currentRoom) {
                            g.setColor(Color.CYAN);
                            g.fillRect(drawX + 5, drawY + 5, 10, 10);
                        } else if (r.isBossRoom() && r.isExplored()) {
                            g.setColor(Color.RED);
                            g.fillRect(drawX + 5, drawY + 5, 10, 10);
                        }
                        g.setColor(Color.BLACK);
                        g.drawRect(drawX, drawY, roomSize, roomSize);
                    }
                }
            }

            if (currentRoom != null && !currentRoom.getEnemies().isEmpty()) {
                g.setFont(new Font("Arial", Font.BOLD, 15));
                g.setColor(Color.WHITE);
                
                int enemyY = 510; 
                g.drawString("Enemies:", marginX, enemyY);
                enemyY += 20;

                int drawnEnemies = 0;
                for (code.model.entities.Enemy enemy : currentRoom.getEnemies()) {
                    if (enemy.isAlive()) {
                        if (drawnEnemies >= 3) break; 
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.PLAIN, 12));
                        g.drawString(enemy.getName() + " (" + enemy.getHp() + "/" + enemy.getMaxHp() + ")", marginX, enemyY);
                        
                        int barWidth = 160;
                        int barHeight = 8; 
                        int barY = enemyY + 5;
                        double hpPercentage = enemy.getHpPercentage();
                        int currentBarWidth = (int) (barWidth * hpPercentage);

                        g.setColor(Color.RED);
                        g.fillRect(marginX, barY, barWidth, barHeight);
                        g.setColor(Color.GREEN);
                        g.fillRect(marginX, barY, currentBarWidth, barHeight);
                        g.setColor(Color.WHITE);
                        g.drawRect(marginX, barY, barWidth, barHeight);
                        enemyY += 30; 
                        drawnEnemies++;
                    }
                }
            }
        }

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.CYAN);
        String lvlText = "LVL " + player.getLevel();
        int lvlX = getWidth() - 80; 
        g.drawString(lvlText, lvlX, safeBottomY);
    }
}