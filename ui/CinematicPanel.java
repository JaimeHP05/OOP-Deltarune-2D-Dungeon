package code.ui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CinematicPanel extends JPanel {
    private int krisX = -50; 
    private final int CONTROLLER_X = 300; 
    private Timer timer;
    private Window window;
    
    private boolean isPlaying = false;

    private BufferedImage floorBackground;
    private BufferedImage consoleOffImg;
    private BufferedImage consoleOnImg;
    private IntroAnimator animator;
    
    private SusieAnimator susieAnimator;
    private int susieDrawWidth;
    private int susieDrawHeight;

    public CinematicPanel(Window window) {
        this.window = window;
        this.animator = new IntroAnimator();
        this.setPreferredSize(new Dimension(0, 120)); // Window.java selects the width
        this.setBackground(Color.BLACK);

        loadAssets();
        initializeSusie();

        timer = new Timer(20, e -> {
            // Move Kris towards console
            if (krisX < CONTROLLER_X - 40) {
                krisX += 2; 
            } else if (!isPlaying) { 
                finishIntro();
            }
            
            susieAnimator.update(); 
            repaint();
        });
    }

    private void loadAssets() {
        try {
            this.floorBackground = ImageIO.read(new File("code/assets/floor.png")); 
            this.consoleOffImg = ImageIO.read(new File("code/assets/console_off.png"));
            this.consoleOnImg = ImageIO.read(new File("code/assets/console_on.png"));
        } catch (IOException e) {
            System.err.println("Warning: Assets missing.");
        }
    }

    private void initializeSusie() {
        int[][] susieCoords = {
            {0, 0, 48, 46}, 
            {53, 0, 48, 46}, 
            {106, 0, 48, 46}
        };
        this.susieAnimator = new SusieAnimator("code/assets/susie2.png", susieCoords);
        this.susieDrawWidth = 120; 
        this.susieDrawHeight = 115;
    }

    public void startAnimation() {
        this.krisX = -50;
        this.isPlaying = false;
        timer.start();
    }

    public void skipAnimation() {
        if (!isPlaying) {
            krisX = CONTROLLER_X - 40; 
            finishIntro();
        }
    }

    private void finishIntro() {
        isPlaying = true;
        this.repaint(); 
        window.showInnerMenu(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Pixel art rendering
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (floorBackground != null) {
            g2d.drawImage(floorBackground, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        int centerY = 90;
        int groundY = centerY + 20;

        int consoleDrawW = 71; 
        int consoleDrawH = 101; 
        int consoleDrawX = CONTROLLER_X - 10; 
        int consoleDrawY = groundY - consoleDrawH - 5; 

        BufferedImage currentConsole = isPlaying ? consoleOnImg : consoleOffImg; // Ternary operator for shortness
        if (currentConsole != null) {
            g2d.drawImage(currentConsole, consoleDrawX, consoleDrawY, consoleDrawW, consoleDrawH, null);
        } else {
            g2d.setColor(isPlaying ? Color.GREEN : Color.GRAY);
            g2d.fillRect(consoleDrawX, consoleDrawY, consoleDrawW, consoleDrawH);
        }

        BufferedImage krisSprite = animator.getSprite(krisX, CONTROLLER_X, isPlaying);
        
        if (krisSprite != null) {
            int krisDrawWidth = 53;  
            int krisDrawHeight = 106; 
            int krisDrawY = groundY - krisDrawHeight;
            g2d.drawImage(krisSprite, krisX, krisDrawY, krisDrawWidth, krisDrawHeight, null);
        }
        
        BufferedImage susieSprite = susieAnimator.getCurrentSprite();
        if (susieSprite != null) {
            int susieDrawX = getWidth() - susieDrawWidth - 50; 
            int susieDrawY = groundY - susieDrawHeight;
            g2d.drawImage(susieSprite, susieDrawX, susieDrawY, susieDrawWidth, susieDrawHeight, null);
        }
    }
}