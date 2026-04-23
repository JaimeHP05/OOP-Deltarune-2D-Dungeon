package code.model.entities;
import code.world.Room;

public class Slime extends Enemy {
    private static final long serialVersionUID = 1L;

    public Slime(int x, int y, int dungeonLevel) {
        super("Phone", x, y, 40 + (dungeonLevel * 10), 8 + dungeonLevel);
        this.setExpReward(5); 
    }

    @Override
    public void onDeath(Player killer, Room room) {
        super.onDeath(killer, room);
        System.out.println("Phone splits into two MicroPhones!");
        int slimesCreated = 0;
        int[] adx = {-1, 1, 0, 0, -1, 1, -1, 1};
        int[] ady = {0, 0, -1, 1, -1, -1, 1, 1};
        
        for (int i = 0; i < 8 && slimesCreated < 2; i++) {
            int tx = this.getX() + adx[i];
            int ty = this.getY() + ady[i];
            
            if (isValidMove(tx, ty, room)) {
                room.addEnemy(new MiniSlime(tx, ty, killer.getCurrentLevel()));
                slimesCreated++;
            }
        }
        
        while (slimesCreated < 2) {
            room.addEnemy(new MiniSlime(this.getX(), this.getY(), killer.getCurrentLevel()));
            slimesCreated++;
        }
    }
}
