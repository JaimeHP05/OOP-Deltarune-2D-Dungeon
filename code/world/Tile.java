package code.world;
import code.model.interfaces.Interactable;
import code.model.entities.Entity;

public class Tile implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected boolean walkable;
    protected Interactable content;

    public Tile(boolean walkable) {
        this.walkable = walkable;
        this.content = null;
    }

    public boolean isWalkable() { return walkable; }
    public Interactable getContent() { return content; }

    public void onEnter(Entity entity) {
        if (content != null) {
            boolean consumed = content.onStep(entity);
            if (consumed) {
                content = null;
            }
        }
    }

    public void put(Interactable content) {
        this.content = content;
    }
}