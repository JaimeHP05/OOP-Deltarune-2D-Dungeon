package code.model.entities;

import code.model.objects.Item;
import code.world.Room;
import code.world.Tile;
import code.model.exceptions.InventoryFullException;
import java.util.List;
import code.model.objects.Inventory;
import code.model.enums.Direction;

public class Player extends Entity {
    private static final long serialVersionUID = 1L;

    private Inventory<Item> inventory;
    private int currentLevel = 1;
    private boolean readyForNextLevel = false;
    
    private Direction facingDirection = Direction.DOWN;
    private boolean isMoving = false;
    
    private int maxHunger = 100;
    private int hunger = 100;
    
    private int exp = 0;
    private int expToNextLevel = 5;
    private int playerLevel = 1; 

    private code.model.objects.Weapon equippedWeapon;
    private int tempDamageBuff = 0;

    private boolean isAttacking = false;
    private boolean isBouncing = false;
    
    private int vampirismLevel = 0;
    private int trapImmunityLevel = 0;
    private int fireLevel = 0;
    private int electricLevel = 0;

    public Player(String name, int startX, int startY) {
        super(name, startX, startY, 100);
        this.inventory = new Inventory<>(3); 
        this.equippedWeapon = new code.model.objects.Weapon("Sword", 15);
    }

    public boolean isAttacking() { return isAttacking; }
    public void startAttackAnimation() { this.isAttacking = true; }
    public void endAttackAnimation() { this.isAttacking = false; }
    
    public Direction getFacingDirection() { return facingDirection; }
    public void setFacingDirection(Direction direction) { this.facingDirection = direction; }
    
    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean moving) { this.isMoving = moving; }

    public void gainExp(int amount) {
        this.exp += amount;
        System.out.println("You gained " + amount + " EXP.");
        while (this.exp >= this.expToNextLevel) levelUp();
    }

    private void levelUp() {
        this.exp -= this.expToNextLevel; 
        this.playerLevel++;
        this.expToNextLevel = this.playerLevel * 5; 
        this.maxHp += 15;
        this.modifyHp(15);
        System.out.println("LEVEL UP! You are now level " + this.playerLevel + ".");
    }

    public int getExp() { return exp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public int getLevel() { return playerLevel; }

    public int getAttackDamage() {
        int base = (equippedWeapon != null) ? equippedWeapon.getTotalDamage() : 1; 
        return base + tempDamageBuff + (getLevel() * 2);
    }

    public code.model.objects.Weapon getEquippedWeapon() { return equippedWeapon; }
    public void equipWeapon(code.model.objects.Weapon newWeapon) { this.equippedWeapon = newWeapon; }
    public void addDamageBuff(int amount) { this.tempDamageBuff += amount; }
    public void clearBuffs() { this.tempDamageBuff = 0; }

    public boolean pickUpItem(Item item) {
        try {
            inventory.add(item);
            return true;
        } catch (InventoryFullException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean useItem(int index) {
        Item item = inventory.getItem(index);
        if (item != null) {
            item.use(this); 
            inventory.remove(item);
            this.onTurnTaken(); 
            return true; 
        }
        return false; 
    }

    public List<Item> getInventoryItems() { return inventory.getItems(); }

    public void reachExit() { this.readyForNextLevel = true; }
    public boolean isReadyForNextLevel() { return readyForNextLevel; }
    public int getCurrentLevel() { return currentLevel; }

    public void advanceLevel() {
        this.currentLevel++;
        this.readyForNextLevel = false;
        this.gainExp(2);
    }

    public void onTurnTaken() {
        if (poisoned) {
            int poisonDamage = Math.max(1, this.maxHp / 100);
            this.modifyHp(-poisonDamage);
        }
    }

    public int getHunger() { return hunger; }
    public void modifyHunger(int amount) {
        this.hunger += amount;
        if (this.hunger > this.maxHunger) this.hunger = this.maxHunger;
        if (this.hunger <= 0) {
            this.hunger = 0;
            this.modifyHp(-1); 
        }
    }

    public int getVampirismLevel() { return vampirismLevel; }
    public void addVampirism() { this.vampirismLevel++; }

    public int getTrapImmunityLevel() { return trapImmunityLevel; }
    public void addTrapImmunity() { this.trapImmunityLevel++; }

    public int getFireLevel() { return fireLevel; }
    public void addFire() { this.fireLevel++; }

    public int getElectricLevel() { return electricLevel; }
    public void addElectric() { this.electricLevel++; }

    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
        this.hp += amount; 
    }

    public boolean hasTrapImmunity() { return trapImmunityLevel > 0; }
    public boolean isBouncing() { return isBouncing; }
    public void setBouncing(boolean bouncing) { this.isBouncing = bouncing; }

    public void attack(Enemy target, Room currentRoom) {
        this.startAttackAnimation();
        int damage = this.getAttackDamage(); 
        
        if (fireLevel > 0) {
            damage += (fireLevel * 10);
            System.out.println("Fire burns the enemy! (+" + (fireLevel * 10) + " DMG)");
        }
        
        target.modifyHp(-damage);
        System.out.println("You attack " + target.getName() + " for " + damage + " damage.");
        
        if (electricLevel > 0) {
            target.immobilize(1);
            System.out.println(target.getName() + " is rooted to the ground!");
        }

        if (vampirismLevel > 0) {
            int heal = vampirismLevel * 2;
            this.modifyHp(heal);
            System.out.println("Vampirism heals you for " + heal + " HP.");
        }
        
        if (!target.isAlive()) {
            target.onDeath(this, currentRoom);
        }
        
        this.onTurnTaken();
    }

    public void attack(Tile targetTile) {
        this.startAttackAnimation();
        
        code.model.interfaces.Interactable content = targetTile.getContent();
        
        if (content instanceof code.model.objects.Trap) {
            code.model.objects.Trap trap = (code.model.objects.Trap) content;
            trap.disarm();
            
            targetTile.put(null);
        } else {
            System.out.println("You swing your weapon at the empty space...");
        }
        this.onTurnTaken();
    }

    public String getStatusText() {
        if (!this.isAlive()) return "DEAD!";
        if (this.isPoisoned()) return "Poisoned";
        return "Healthy";
    }
}