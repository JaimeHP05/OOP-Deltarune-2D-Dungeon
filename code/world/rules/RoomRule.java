package code.world.rules;

import code.model.entities.Player;

public interface RoomRule extends java.io.Serializable {
    
    void applyEffect(Player player);
    String getDescription();
}