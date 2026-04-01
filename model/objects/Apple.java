package code.model.objects;
import code.model.entities.Entity;
import code.model.entities.Player;

public class Apple extends Item {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    public Apple() { super("Apple"); }

    @Override
    public void use(Entity entity) {
        if (entity instanceof Player) {
            System.out.println("Yummy. You gained 15 hunger.");
            ((Player) entity).modifyHunger(15);
        }
    }
}