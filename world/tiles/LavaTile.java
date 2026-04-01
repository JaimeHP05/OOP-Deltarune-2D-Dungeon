package code.world.tiles;
import code.world.Tile;
import code.model.entities.Entity;

public class LavaTile extends Tile {
    private static final long serialVersionUID = 1L;
    
    public LavaTile() {
        super(true);
    }

    @Override
    public void onEnter(Entity entity) {
        System.out.println("It hurts! You lost 5 HP.");
        entity.modifyHp(-5);
        super.onEnter(entity);
    }
}