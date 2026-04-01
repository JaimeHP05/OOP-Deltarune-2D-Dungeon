package code.world.rules;

import code.model.entities.Player;

public class PoisonGasRule implements RoomRule {
    private static final long serialVersionUID = 1L;
    @Override
    public void applyEffect(Player player) {
        if (!player.hasTrapImmunity()) {
            System.out.println("Toxic gas damages you! (-1 HP)");
            player.modifyHp(-1);
        }
    }

    @Override
    public String getDescription() {
        return "Poison Gas: You lose HP every turn.";
    }
}