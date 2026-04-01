package code.model.objects;
import code.model.interfaces.Interactable;
import code.model.entities.Entity;

public class Trap implements Interactable {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    private int damage;

    public Trap(int damage) {
        this.damage = damage;
    }

    @Override
    public boolean onStep(Entity entity) {
        System.out.println(entity.getName() + " touched a trap. They lose " + damage + " HP.");
        entity.modifyHp(-damage);
        return true;
    }

    public void disarm() {
        this.damage = 0; 
        System.out.println("CLICK. The trap mechanism has been safely disarmed.");
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}