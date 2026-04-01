package code.model.entities;

public class Snake extends Enemy {
    private static final long serialVersionUID = 1L;
    
    public Snake(int startX, int startY, int dungeonLevel) {
        super("Scorpion", startX, startY, 20, 5, dungeonLevel, 3);
    }

    @Override
    public void attack(Entity target, int damage) {
        super.attack(target, damage);
        
        if (!target.isPoisoned()) {
            target.setPoisoned(true);
            System.out.println("You got poisoned!");
        }
    }
}