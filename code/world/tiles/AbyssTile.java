package code.world.tiles;
import code.world.Tile;
import code.model.entities.Entity;

public class AbyssTile extends Tile {
    private static final long serialVersionUID = 1L;
    
    public AbyssTile() {
        super(false);
    }

    @Override
    public void onEnter(Entity entity) {
        System.out.println("You have fallen into the abyss...");
        entity.modifyHp(-9999);
    }
}