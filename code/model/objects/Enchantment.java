package code.model.objects;

public class Enchantment implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int bonusDamage;

    public Enchantment(String name, int bonusDamage) {
        this.name = name;
        this.bonusDamage = bonusDamage;
    }

    public String getName() { return name; }
    public int getBonusDamage() { return bonusDamage; }
}