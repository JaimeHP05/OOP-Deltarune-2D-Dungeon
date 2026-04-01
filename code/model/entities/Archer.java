package code.model.entities;

public class Archer extends Enemy {
    private static final long serialVersionUID = 1L;
    
    public Archer(int x, int y, int dungeonLevel) {
        super("Magician", x, y, 30 + (dungeonLevel * 10), 5 + (dungeonLevel * 2));
        this.expReward = 4;
    }

    @Override
    public void takeTurn(Player player, code.world.Room room) {
        if (!this.isAlive()) return;

        double distance = Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2));

        if (distance > 1.5 && distance <= 3.5) {
            System.out.println("The " + this.getName() + " shoots a proyectile from afar! It hits you for " + this.damage + " damage.");
            this.attack(player, this.damage); 
            return; 
        }

        super.takeTurn(player, room);
    }
}