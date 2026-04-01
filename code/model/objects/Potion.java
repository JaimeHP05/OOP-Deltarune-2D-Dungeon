package code.model.objects;
import code.model.entities.Entity;

public class Potion extends Item {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    private int healAmount;

    public Potion(int healAmount) {
        super("Potion");
        this.healAmount = healAmount;
    }

    @Override
    public void use(Entity entity) {
        System.out.println("You drink the " + getName() + ". You restore " + healAmount + " HP.");
        entity.modifyHp(healAmount);
    }
}