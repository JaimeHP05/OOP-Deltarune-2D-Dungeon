package code.model.objects.upgrades;
import code.model.entities.Player;

public class AgilityUpgrade implements UpgradeStrategy {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void apply(Player player) {
        player.addTrapImmunity();
    }

    @Override
    public String getMessage() {
        return "Trap Immunity! Traps won't hurt you.";
    }
}