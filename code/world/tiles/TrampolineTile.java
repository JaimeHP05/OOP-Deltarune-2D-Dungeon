package code.world.tiles;

import code.world.Tile;
import code.model.entities.Entity;
import code.model.entities.Player;

public class TrampolineTile extends Tile {
    private static final long serialVersionUID = 1L;
    
    public TrampolineTile() {
        super(true);
    }

    @Override
    public void onEnter(Entity entity) {
        if (entity instanceof Player) {
            System.out.println("Boing! The trampoline propels you over one tile.");
            Player p = (Player) entity;
            p.setBouncing(true);
        }
        super.onEnter(entity);
    }
}