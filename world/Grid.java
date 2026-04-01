package code.world;

import code.model.entities.Enemy;
import code.model.entities.Player;
import code.model.interfaces.Interactable;

public class Grid implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final int ROOM_W = 12;
    private final int ROOM_H = 13;
    private final int GRID_SIZE_X = 5;
    private final int GRID_SIZE_Y = 5;
    
    private int currentLevel;
    private int spawnX, spawnY;
    private Room[][] dungeonMap;
    private Room currentRoom;
    private int currentRoomGridX, currentRoomGridY;

    public Grid(int width, int height, int currentLevel) {
        this.currentLevel = currentLevel;

        DungeonGenerator generator = new DungeonGenerator();
        this.dungeonMap = generator.generate(currentLevel);
        
        this.currentRoomGridX = 2;
        this.currentRoomGridY = 2;
        this.currentRoom = dungeonMap[currentRoomGridX][currentRoomGridY];
        
        findSpawnCoordinates(currentRoom);
    }

    private void findSpawnCoordinates(Room room) {
        this.spawnX = ROOM_W / 2;
        this.spawnY = ROOM_H / 2;
    }

    public int getCurrentLevel() { return currentLevel; }
    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    public int getWidth() { return ROOM_W; }
    public int getHeight() { return ROOM_H; }
    public Room getCurrentRoom() { return currentRoom; }
    public Room[][] getDungeonMap() { return dungeonMap; }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= ROOM_W || y < 0 || y >= ROOM_H) return null;
        return currentRoom.getTile(x, y);
    }

    public Enemy getEnemyAt(int x, int y) {
        if (currentRoom == null) return null;
        for (Enemy e : currentRoom.getEnemies()) {
            if (e.isAlive() && e.getX() == x && e.getY() == y) return e;
        }
        return null;
    }

    public void updateEnemies(Player player) {
        if (currentRoom != null && currentRoom.getEnemies() != null) {
            for (Enemy enemy : currentRoom.getEnemies()) {
                if (enemy.isAlive()) {
                    enemy.takeTurn(player, currentRoom);
                }
            }
        }
    }

    public boolean move(Player player, int dx, int dy) {
        int nx = player.getX() + dx;
        int ny = player.getY() + dy;

        Tile targetTile = getTile(nx, ny);

        if (targetTile == null) {
            return attemptRoomTransition(player, dx, dy);
        }

        if (targetTile.isWalkable()) {
            Enemy enemy = getEnemyAt(nx, ny);

            if (enemy != null && enemy.isAlive()) {
                player.attack(enemy, currentRoom);
            } else {
                Interactable content = targetTile.getContent();
                
                if (content != null) {
                    if (content.isSolid()) {
                        boolean consumed = content.onStep(player);
                        if (consumed) targetTile.put(null);
                    } else {
                        player.setPosition(nx, ny);
                        
                        if (content instanceof code.model.objects.Trap && player.hasTrapImmunity()) {
                            System.out.println("You pass safely over the trap thanks to your Trap Immunity.");
                            targetTile.put(null); 
                        } else {
                            boolean consumed = content.onStep(player);
                            if (consumed) targetTile.put(null);
                        }
                    }
                } else {
                    player.setPosition(nx, ny);
                    targetTile.onEnter(player);
                }

                if (player.isBouncing()) {
                    player.setBouncing(false); 
                    System.out.println("Whoosh! You fly over the next tile!");
                    this.move(player, dx * 2, dy * 2);
                    return true; 
                }
            }
            player.onTurnTaken();
            applyRoomRules(player);
            return true;
        }
        return false;
    }
    
    private void applyRoomRules(Player player) {
        if (currentRoom != null) {
            currentRoom.applyRules(player);
        }
    }

    private boolean attemptRoomTransition(Player player, int dx, int dy) {
        int nextRoomX = currentRoomGridX + dx;
        int nextRoomY = currentRoomGridY + dy;

        if (nextRoomX >= 0 && nextRoomX < GRID_SIZE_X && nextRoomY >= 0 && nextRoomY < GRID_SIZE_Y && dungeonMap[nextRoomX][nextRoomY] != null) {
            
            dungeonMap[nextRoomX][nextRoomY].setExplored(true);
            currentRoom = dungeonMap[nextRoomX][nextRoomY];
            currentRoomGridX = nextRoomX;
            currentRoomGridY = nextRoomY;

            if (dx == 1) player.setPosition(0, player.getY());
            else if (dx == -1) player.setPosition(ROOM_W - 1, player.getY());
            else if (dy == 1) player.setPosition(player.getX(), 0);
            else if (dy == -1) player.setPosition(player.getX(), ROOM_H - 1);

            player.clearBuffs();

            return true;
        }
        return false;
    }
}