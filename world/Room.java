package code.world;

import code.model.entities.Enemy;
import code.model.entities.Player;
import code.world.rules.RoomRule;
import java.util.ArrayList;
import java.util.List;

public class Room implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Tile[][] tiles;
    private List<Enemy> enemies;
    private int width;
    private int height;
    
    private boolean isExplored = false;
    private boolean isBossRoom = false; 

    private List<RoomRule> rules;

    public Room(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.enemies = new ArrayList<>();
        this.rules = new ArrayList<>();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return tiles[x][y];
    }

public void setTile(int x, int y, Tile tile) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new code.model.exceptions.IllegalMoveException("Attempted to place a tile out of room bounds at: " + x + ", " + y);
        }
        tiles[x][y] = tile;
    }

    public List<Enemy> getEnemies() { return enemies; }
    public void addEnemy(Enemy enemy) { this.enemies.add(enemy); }

    public Enemy getEnemyAt(int x, int y) {
        for (Enemy e : enemies) {
            if (e.isAlive() && e.getX() == x && e.getY() == y) return e;
        }
        return null;
    }

    public boolean isExplored() { return isExplored; }
    public void setExplored(boolean explored) { this.isExplored = explored; }

    public boolean isBossRoom() { return isBossRoom; }
    public void setBossRoom(boolean bossRoom) { this.isBossRoom = bossRoom; }

    public void addRule(RoomRule rule) {
        this.rules.add(rule);
    }

    public void applyRules(Player player) {
        for (RoomRule rule : rules) {
            rule.applyEffect(player);
        }
    }
    
    public List<RoomRule> getRules() {
        return rules;
    }
}