package code.model.entities;

public abstract class Entity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int x;
    private int y;
    private int hp;
    private int maxHp;
    
    private boolean poisoned = false;

    public Entity(String name, int startX, int startY, int maxHp) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.maxHp = maxHp;
        this.hp = maxHp; 
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public String getName() { return name; }

    public void modifyHp(int amount) {
        this.hp += amount;
        if (this.hp > this.maxHp) this.hp = this.maxHp;
        if (this.hp < 0) this.hp = 0;
        
        if (this.hp > 0) {
            System.out.println("-> Current HP of " + name + ": " + hp + "/" + maxHp);
        }
    }

    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
        this.hp += amount; 
    }

    public boolean isAlive() { return this.hp > 0; }

    public boolean isPoisoned() { return poisoned; }
    public void setPoisoned(boolean poisoned) { this.poisoned = poisoned; }

    public void attack(Entity target, int damage) {
        System.out.println(this.getName() + " attacks " + target.getName() + " and deals " + damage + " damage!");
        target.modifyHp(-damage);
    }

    public void attack() {
        System.out.println(this.getName() + " attacks the air... Misses!");
    }

    public double getHpPercentage() {
        return Math.max(0.0, (double) this.hp / this.maxHp);
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
}
