package code.model.objects;

import code.model.entities.Player;
import code.model.interfaces.Interactable;

public class Chest implements Interactable {
    private Item contentItem;
    private boolean isOpen;

    public Chest(Item item) {
        this.contentItem = item;
        this.isOpen = false;
    }

    public void open(Player player) {
        if (!isOpen) {
            System.out.println("You open the chest...");
            boolean pickedUp = player.pickUpItem(contentItem);
            
            if (pickedUp) {
                isOpen = true; 
            } else {
                System.out.println("Your inventory is full, you must empty it to pick up: " + contentItem.getName());
            }
        }
    }

    public boolean isOpen() { 
        return isOpen; 
    }

    @Override
    public boolean onStep(code.model.entities.Entity entity) {
        if (entity instanceof Player) {
            this.open((Player) entity);
        }
        return false; 
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}