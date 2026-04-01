package code.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InnerMenuPanel extends JPanel {
    
    private static final String INTRO_PATH = "code/assets/intro/";

    public InnerMenuPanel(Window window) {
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.BLACK); 

        JPanel centralPanel = new JPanel();
        centralPanel.setOpaque(false); 
        centralPanel.setLayout(new BorderLayout(0, 30));

        AnimatedTitle animatedTitle = new AnimatedTitle();
        centralPanel.add(animatedTitle, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        buttonPanel.setOpaque(false);

        JButton btnNew = new JButton("New Game");
        JButton btnLoad = new JButton("Continue");
        JButton btnExit = new JButton("Exit");

        btnNew.setFocusPainted(false);
        btnLoad.setFocusPainted(false);
        btnExit.setFocusPainted(false);

        btnNew.addActionListener(e -> window.startGame(false));
        btnLoad.addActionListener(e -> window.startGame(true));
        btnExit.addActionListener(e -> {
            System.out.println("Closing game...");
            System.exit(0);
        });

        buttonPanel.add(btnNew);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnExit);
        
        centralPanel.add(buttonPanel, BorderLayout.CENTER);
        this.add(centralPanel);
    }

    private static class AnimatedTitle extends JComponent {
        private BufferedImage[] frames;
        private BufferedImage currentFrame;
        
        private int currentFrameIndex = 6;
        private final int INTRO_END_FRAME = 163; 
        private final int LOOP_START_FRAME = 140;
        private final int LOOP_END_FRAME = 163;   
        
        private boolean isIntroFinished = false;
        private Timer animationTimer;

        public AnimatedTitle() {
            frames = new BufferedImage[INTRO_END_FRAME + 1];
            preloadFrames();
            
            currentFrame = frames[currentFrameIndex];
            
            if (currentFrame != null) {
                this.setPreferredSize(new Dimension(currentFrame.getWidth(), currentFrame.getHeight()));
            } else {
                this.setPreferredSize(new Dimension(300, 100));
            }

            animationTimer = new Timer(41, event -> {
                currentFrameIndex++;
                
                if (!isIntroFinished) {
                    if (currentFrameIndex > INTRO_END_FRAME) {
                        isIntroFinished = true;
                        currentFrameIndex = LOOP_START_FRAME; 
                    }
                } else {
                    if (currentFrameIndex > LOOP_END_FRAME) {
                        currentFrameIndex = LOOP_START_FRAME;
                    }
                }
                
                currentFrame = frames[currentFrameIndex];
                this.repaint();
            });
            animationTimer.start();
        }

        private void preloadFrames() {
            for (int i = 6; i <= INTRO_END_FRAME; i++) {
                String filename = "spr_dw_tv_time_intro_" + i + ".png";
                File file = new File(INTRO_PATH + filename);
                try {
                    frames[i] = ImageIO.read(file);
                } catch (IOException e) {
                    if (i == 6) {
                        System.err.println("Error: Missing intro at: " + file.getAbsolutePath());
                    }
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentFrame != null) {
                int x = (getWidth() - currentFrame.getWidth()) / 2;
                int y = (getHeight() - currentFrame.getHeight()) / 2;
                g.drawImage(currentFrame, x, y, null);
            }
        }
    }
}