package code.model.entities;

public class MiniSlime extends Enemy {
    private static final long serialVersionUID = 1L;
    
    public MiniSlime(int x, int y, int dungeonLevel) {
        super("MicroPhone", x, y, 15 + (dungeonLevel * 5), 4 + dungeonLevel);
        this.expReward = 1;
    }
}