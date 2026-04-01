package code.model.objects;

import code.model.entities.Player;
import code.model.interfaces.Interactable;
import code.model.objects.upgrades.UpgradeStrategy;

public class UpgradeStation implements Interactable {
    private boolean isUsed = false;
    private UpgradeStrategy strategy;

    public UpgradeStation(UpgradeStrategy strategy) {
        this.strategy = strategy;
    }

    public void interact(Player player) {
        if (isUsed) return;
        strategy.apply(player);
        System.out.println(strategy.getMessage());
        isUsed = true;
    }

    public boolean isUsed() { return isUsed; }

    @Override
    public boolean onStep(code.model.entities.Entity entity) {
        if (entity instanceof Player) {
            this.interact((Player) entity);
            return true;
        }
        return false; 
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}