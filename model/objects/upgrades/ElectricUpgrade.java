package code.model.objects.upgrades;
import code.model.entities.Player;

public class ElectricUpgrade implements UpgradeStrategy {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void apply(Player player) {
        player.addElectric();
    }

    @Override
    public String getMessage() {
        return "Electric magic! Your attacks will root enemies.";
    }
}