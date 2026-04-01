package code.model.objects.upgrades;
import code.model.entities.Player;

public class FireUpgrade implements UpgradeStrategy {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void apply(Player player) {
        player.addFire();
    }

    @Override
    public String getMessage() {
        return "Fire magic! Your attacks deal more damage.";
    }
}