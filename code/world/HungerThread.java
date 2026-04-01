package code.world;

import code.model.entities.Player;
import javax.swing.JPanel;

public class HungerThread extends Thread {
    private Player player;
    private JPanel panel;
    private volatile boolean running = true;

    public HungerThread(Player player, JPanel panel) {
        this.player = player;
        this.panel = panel;
    }

    public void stopThread() { 
        this.running = false; 
    }

    @Override
    public void run() {
        while (running && player.isAlive()) {
            try {
                Thread.sleep(1000); 
                
                if (running && player.isAlive()) {
                    player.modifyHunger(-1);
                    if (panel != null) panel.repaint();
                }
            } catch (InterruptedException e) {
                System.out.println("Hunger thread interrupted.");
            }
        }
    }
}