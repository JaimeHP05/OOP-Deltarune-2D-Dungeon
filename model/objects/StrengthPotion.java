package code.model.objects;
import code.model.entities.Entity;
import code.model.entities.Player;

public class StrengthPotion extends Item {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    public StrengthPotion() { 
        super("Strength Potion"); 
    }

    @Override
    public void use(Entity entity) {
        if (entity instanceof Player) {
            Player p = (Player) entity;
            int buff = Math.max(1, (int)(p.getAttackDamage() * 0.25)); 
            System.out.println("You drink the " + getName() + ".");
            p.addDamageBuff(buff);
        }
    }
}