package code.model.objects.upgrades;
import code.model.entities.Player;

public class VampirismUpgrade implements UpgradeStrategy {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void apply(Player player) {
        player.addVampirism();
    }

    @Override
    public String getMessage() {
        return "Vampirism! You heal when attacking.";
    }
}