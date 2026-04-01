package code.model.objects.upgrades;

import code.model.entities.Player;

public interface UpgradeStrategy extends java.io.Serializable {
    
    void apply(Player player);
    String getMessage();
}