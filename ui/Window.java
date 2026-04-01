package code.ui;

import code.model.entities.Player;
import code.world.Grid;
import code.world.HungerThread;
import code.utils.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import code.model.enums.Direction;

public class Window extends JFrame implements KeyListener {
    private Grid grid;
    private Player player;
    private BufferedImage globalFrame;
    
    private JPanel mainContainer;
    private GamePanel gamePanel;
    private HUDPanel hudPanel;
    private CinematicPanel cinematicPanel;
    
    private JPanel leftBlock;
    private JPanel screenContainer;
    
    private HungerThread hungerThread;
    
    private enum GameState { INTRO, MENU, PLAYING }
    private GameState currentState = GameState.INTRO;

    public Window() {
        this.setTitle("OOP Project: Deltarune 2D Dungeon");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700); 
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.addKeyListener(this);
        this.setFocusable(true);
        
        mainContainer = new JPanel(new BorderLayout());
        
        this.hudPanel = new HUDPanel(null); 
        this.cinematicPanel = new CinematicPanel(this);
        
        JPanel tvOffScreen = new JPanel();
        tvOffScreen.setBackground(Color.BLACK);
        
        try {
            this.globalFrame = ImageIO.read(new File("code/assets/frame.png"));
        } catch (IOException e) {
            System.err.println("Warning: Frame asset not found.");
        }

        screenContainer = new JPanel(new BorderLayout()) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (globalFrame != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2d.drawImage(globalFrame, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        
        screenContainer.add(tvOffScreen, BorderLayout.CENTER);
        
        leftBlock = new JPanel(new BorderLayout());
        leftBlock.add(screenContainer, BorderLayout.CENTER); 
        leftBlock.add(cinematicPanel, BorderLayout.SOUTH);

        mainContainer.add(leftBlock, BorderLayout.CENTER);
        mainContainer.add(hudPanel, BorderLayout.EAST);

        this.setContentPane(mainContainer);
        
        currentState = GameState.INTRO;
        cinematicPanel.startAnimation(); 
        
        this.setVisible(true);
    }

    private void setScreenCenter(Component newCenter) {
        if (screenContainer != null) {
            BorderLayout layout = (BorderLayout) screenContainer.getLayout();
            Component oldCenter = layout.getLayoutComponent(BorderLayout.CENTER);
            if (oldCenter != null) screenContainer.remove(oldCenter);
            
            screenContainer.add(newCenter, BorderLayout.CENTER);
            
            screenContainer.revalidate();
            screenContainer.repaint();
        }
    }

    public void showInnerMenu() {
        currentState = GameState.MENU;
        hudPanel.setSystemStatus("");
        hudPanel.setPlayer(null);
        hudPanel.setGrid(null);
        hudPanel.repaint();
        
        gamePanel = null;
        setScreenCenter(new InnerMenuPanel(this));
        
        this.requestFocus(); 
    }

    public void startGame(boolean loadSave) {
        boolean loaded = false;
        if (loadSave) {
            Object[] data = SaveManager.loadGame();
            if (data != null) {
                this.grid = (Grid) data[0];
                this.player = (Player) data[1];
                loaded = true;
            }
        }
        if (!loaded) {
            this.grid = new Grid(25, 20, 1);
            this.player = new Player("Kris", grid.getSpawnX(), grid.getSpawnY());
        }

        hudPanel.setPlayer(player);
        hudPanel.setGrid(grid); 
        hudPanel.setVisible(true);
        hudPanel.setSystemStatus("");
        
        this.gamePanel = new GamePanel(grid, player);
        setScreenCenter(gamePanel);
        currentState = GameState.PLAYING;
        
        if (hungerThread != null) hungerThread.stopThread();
        this.hungerThread = new HungerThread(player, hudPanel); 
        this.hungerThread.start();
        
        this.requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (currentState == GameState.INTRO) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) cinematicPanel.skipAnimation();
            return; 
        }

        if (currentState == GameState.MENU) return;
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (hungerThread != null) hungerThread.stopThread();
            SaveManager.saveGame(grid, player);
            showInnerMenu(); 
            return;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_G) {
            SaveManager.saveGame(grid, player);
            hudPanel.setSystemStatus("GAME SAVED!"); 
            return;
        }

        if (!player.isAlive()) return;

        Direction moveDir = null;
        boolean itemUsed = false;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    moveDir = Direction.UP; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN:  moveDir = Direction.DOWN; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  moveDir = Direction.LEFT; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: moveDir = Direction.RIGHT; break;
            case KeyEvent.VK_1: itemUsed = player.useItem(0); break;
            case KeyEvent.VK_2: itemUsed = player.useItem(1); break;
            case KeyEvent.VK_3: itemUsed = player.useItem(2); break;
            
            case KeyEvent.VK_SPACE:
                Direction facing = player.getFacingDirection();
                int targetX = player.getX() + facing.getDx();
                int targetY = player.getY() + facing.getDy();
                
                code.model.entities.Enemy targetEnemy = grid.getEnemyAt(targetX, targetY);
                if (targetEnemy != null && targetEnemy.isAlive()) {
                    player.attack(targetEnemy, grid.getCurrentRoom());
                } else {
                    code.world.Tile targetTile = grid.getTile(targetX, targetY);
                    if (targetTile != null) {
                        player.attack(targetTile);
                    }
                }
                itemUsed = true; 
                break;
        }

        boolean turnTaken = false;
        if (moveDir != null) {
            player.setFacingDirection(moveDir);
            player.setMoving(true);
            turnTaken = grid.move(player, moveDir.getDx(), moveDir.getDy());
        }

        if (turnTaken || itemUsed) {
            grid.updateEnemies(player);
            hudPanel.setSystemStatus("PRESS G TO SAVE");
            
            screenContainer.repaint();
            hudPanel.repaint();
            
            Timer animTimer = new Timer(150, event -> {
                player.setMoving(false);
                player.endAttackAnimation(); 
                screenContainer.repaint(); 
            });
            animTimer.setRepeats(false); 
            animTimer.start();
        }

        if (player.isReadyForNextLevel()) {
            player.advanceLevel();
            this.grid = new Grid(25, 20, player.getCurrentLevel()); 
            player.setPosition(grid.getSpawnX(), grid.getSpawnY());
            
            hudPanel.setGrid(this.grid);
            this.gamePanel = new GamePanel(grid, player);
            setScreenCenter(gamePanel);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}