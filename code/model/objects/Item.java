package code.model.objects;
import code.model.interfaces.Interactable;
import code.model.entities.Entity;
import code.model.entities.Player;

public abstract class Item implements Interactable {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    private String name;

    public Item(String name) { this.name = name; }
    public String getName() { return name; }

    public abstract void use(Entity entity);

    @Override
    public boolean onStep(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            return player.pickUpItem(this);
        }
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
