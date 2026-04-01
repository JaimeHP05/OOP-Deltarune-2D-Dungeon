package code.model.entities;

public class Orc extends Enemy {
    private static final long serialVersionUID = 1L;

    public Orc(int startX, int startY, int dungeonLevel) {
        super("Biggie", startX, startY, 60, 18, dungeonLevel, 5);
    }
}