package code.model.entities;

import code.world.Room;
import code.model.objects.Stairs;

public class Boss extends Enemy {
    private static final long serialVersionUID = 1L;
    
    public Boss(int x, int y, int dungeonLevel) {
        super("ERAM", x, y, 150 + (dungeonLevel * 50), 12 + (dungeonLevel * 3));
        this.expReward = 20; 
    }

    @Override
    public void onDeath(Player killer, Room room) {
        super.onDeath(killer, room); 
        
        System.out.println("The Boss has been defeated! A secret staircase appears...");
        code.world.Tile tile = room.getTile(this.getX(), this.getY());
        if (tile != null) {
            tile.put(new Stairs());
        }
    }
}