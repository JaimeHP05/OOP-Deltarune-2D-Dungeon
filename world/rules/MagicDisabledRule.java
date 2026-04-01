package code.world.rules;

import code.model.entities.Player;

public class MagicDisabledRule implements RoomRule {
    private static final long serialVersionUID = 1L;
    @Override
    public void applyEffect(Player player) {
        player.clearBuffs();
    }

    @Override
    public String getDescription() {
        return "Anti-Magic Zone: All temporary buffs are instantly removed.";
    }
}