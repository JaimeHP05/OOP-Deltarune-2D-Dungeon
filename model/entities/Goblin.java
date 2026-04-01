package code.model.entities;

public class Goblin extends Enemy {
    private static final long serialVersionUID = 1L;
    
    public Goblin(int startX, int startY, int dungeonLevel) {
        super("Ruddin", startX, startY, 30, 10, dungeonLevel, 2);
    }
}