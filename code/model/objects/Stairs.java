package code.model.objects;

import code.model.interfaces.Interactable;
import code.model.entities.Entity;
import code.model.entities.Player;

public class Stairs implements Interactable {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    @Override
    public boolean onStep(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.reachExit();
            return true;
        }
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}