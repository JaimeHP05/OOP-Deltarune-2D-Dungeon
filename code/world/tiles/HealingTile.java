package code.world.tiles;
import code.world.Tile;
import code.model.entities.Entity;
import code.model.entities.Player;

public class HealingTile extends Tile {
    private static final long serialVersionUID = 1L;
    
    private boolean isUsed = false;

    public HealingTile() {
        super(true);
    }

    @Override
    public void onEnter(Entity entity) {
        if (!isUsed && entity instanceof Player) {
            Player p = (Player) entity;
            System.out.println("Holy place. You regain 20 HP and are cured from poison.");
            p.modifyHp(20);
            p.setPoisoned(false); 
            this.isUsed = true;
        }
        super.onEnter(entity);
    }
}