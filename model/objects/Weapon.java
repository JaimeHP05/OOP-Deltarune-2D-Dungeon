package code.model.objects;
import code.model.entities.Player;

public class Weapon extends Item {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private int baseDamage;
    private Enchantment enchantment; 

    public Weapon(String name, int baseDamage) {
        super(name);
        this.baseDamage = baseDamage;
        this.enchantment = null; 
    }

    public void applyEnchantment(Enchantment e) {
        this.enchantment = e;
        System.out.println("The weapon " + this.getName() + " has been enchanted with: " + e.getName());
    }

    public int getTotalDamage() {
        int total = baseDamage;
        if (enchantment != null) {
            total += enchantment.getBonusDamage();
        }
        return total;
    }

    public String getFullName() {
        if (enchantment != null) return getName() + " (" + enchantment.getName() + ")";
        return getName();
    }

    @Override
    public void use(code.model.entities.Entity entity) {
        if (entity instanceof Player) {
            Player p = (Player) entity;
            p.equipWeapon(this);
        } else {
            System.out.println("Enemies cannot use this weapon.");
        }
    }
}