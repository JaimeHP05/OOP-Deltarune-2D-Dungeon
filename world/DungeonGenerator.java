package code.world;

import code.model.entities.*;
import code.model.objects.*;
import code.world.tiles.*;
import code.world.rules.*;
import code.model.objects.upgrades.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

public class DungeonGenerator {
    private List<String> normalRoomFiles;
    private final int ROOM_W = 12;
    private final int ROOM_H = 13;
    private final int GRID_SIZE_X = 5;
    private final int GRID_SIZE_Y = 5;
    private final int MIN_ROOMS = 2;
    private final int MAX_ROOMS = 4;

    private final Map<Character, Supplier<Tile>> tileRegistry;
    private final Map<Character, Supplier<code.model.interfaces.Interactable>> interactableRegistry;
    private final Map<Character, EnemyFactory> enemyRegistry;

    @FunctionalInterface
    private interface EnemyFactory {
        Enemy create(int x, int y, int level);
    }

    public DungeonGenerator() {
        tileRegistry = new HashMap<>();
        interactableRegistry = new HashMap<>();
        enemyRegistry = new HashMap<>();
        
        registerFactories();
        loadAvailableRoomFiles();
    }

    private void registerFactories() {
        tileRegistry.put('#', () -> new Tile(false));
        tileRegistry.put('~', () -> new LavaTile());
        tileRegistry.put('+', () -> new HealingTile());
        tileRegistry.put('C', () -> new AbyssTile());
        tileRegistry.put('J', () -> new TrampolineTile());

        interactableRegistry.put('T', () -> new Trap(20));
        interactableRegistry.put('E', () -> new Stairs());
        interactableRegistry.put('P', () -> new Chest(new Potion(30)));
        interactableRegistry.put('F', () -> new Chest(new StrengthPotion()));
        interactableRegistry.put('W', () -> {
            code.model.objects.Weapon magicSword = new code.model.objects.Weapon("Excalibur", 20);
            magicSword.applyEnchantment(new code.model.objects.Enchantment("Holy Light", 10));
            return new code.model.objects.Chest(magicSword);
        });
        interactableRegistry.put('A', () -> new Chest(new Apple()));
        interactableRegistry.put('B', () -> new Chest(new Berry()));
        
        interactableRegistry.put('V', () -> new UpgradeStation(new VampirismUpgrade()));
        interactableRegistry.put('I', () -> new UpgradeStation(new AgilityUpgrade()));
        interactableRegistry.put('M', () -> new UpgradeStation(new ElectricUpgrade())); 
        interactableRegistry.put('Q', () -> new UpgradeStation(new FireUpgrade())); 

        interactableRegistry.put('*', () -> {
            UpgradeStrategy[] pool = {
                new VampirismUpgrade(), 
                new AgilityUpgrade(), 
                new FireUpgrade(), 
                new ElectricUpgrade()
            };
            UpgradeStrategy randomStrategy = pool[new java.util.Random().nextInt(pool.length)];
            return new UpgradeStation(randomStrategy);
        });

        enemyRegistry.put('1', (x, y, lvl) -> new MiniSlime(x, y, lvl));
        enemyRegistry.put('2', (x, y, lvl) -> new Slime(x, y, lvl));
        enemyRegistry.put('3', (x, y, lvl) -> new Goblin(x, y, lvl));
        enemyRegistry.put('4', (x, y, lvl) -> new Snake(x, y, lvl));
        enemyRegistry.put('5', (x, y, lvl) -> new Archer(x, y, lvl));
        enemyRegistry.put('6', (x, y, lvl) -> new Orc(x, y, lvl));
        enemyRegistry.put('X', (x, y, lvl) -> new Boss(x, y, lvl));
    }

    public Room[][] generate(int currentLevel) {
        Room[][] dungeonMap = new Room[GRID_SIZE_X][GRID_SIZE_Y];
        int numRoomsToPlace = MIN_ROOMS + (int)(Math.random() * ((MAX_ROOMS - MIN_ROOMS) + 1));
        
        dungeonMap[2][2] = createRoomFromFile("code/assets/rooms/beginning.txt", currentLevel, false);
        
        int placedRoomsCount = 0;
        List<Point> openPoints = new ArrayList<>();
        addNeighborsToPoints(2, 2, openPoints, dungeonMap);

        while (placedRoomsCount < numRoomsToPlace && !openPoints.isEmpty()) {
            int randomIndex = (int)(Math.random() * openPoints.size());
            Point p = openPoints.remove(randomIndex);
            
            if (dungeonMap[p.x][p.y] != null) continue;

            String selectedRoomFile = normalRoomFiles.get((int)(Math.random() * normalRoomFiles.size()));
            dungeonMap[p.x][p.y] = createRoomFromFile(selectedRoomFile, currentLevel, true);
            
            if (Math.random() < 0.3) {
                dungeonMap[p.x][p.y].addRule(new PoisonGasRule());
            }
            if (Math.random() > 0.7) {
                dungeonMap[p.x][p.y].addRule(new MagicDisabledRule());
            }
            
            placedRoomsCount++;
            addNeighborsToPoints(p.x, p.y, openPoints, dungeonMap);
        }

        if (!openPoints.isEmpty()) {
            Point p = openPoints.remove(openPoints.size() - 1);
            if (dungeonMap[p.x][p.y] == null) {
                dungeonMap[p.x][p.y] = createRoomFromFile("code/assets/rooms/boss_1.txt", currentLevel, true);
                dungeonMap[p.x][p.y].setBossRoom(true);
            }
        }

        punchAutomaticDoors(dungeonMap);
        return dungeonMap;
    }

    private Room createRoomFromFile(String filepath, int currentLevel, boolean spawnEnemies) {
        Room room = new Room(ROOM_W, ROOM_H);
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            int y = 0;
            while ((line = br.readLine()) != null && y < ROOM_H) {
                for (int x = 0; x < ROOM_W && x < line.length(); x++) {
                    char c = line.charAt(x);
                    
                    Tile currentTile;
                    if (tileRegistry.containsKey(c)) {
                        currentTile = tileRegistry.get(c).get();
                    } else {
                        currentTile = new Tile(true); 
                    }
                    room.setTile(x, y, currentTile);

                    if (interactableRegistry.containsKey(c)) {
                        currentTile.put(interactableRegistry.get(c).get());
                    }
                    
                    if (spawnEnemies && enemyRegistry.containsKey(c)) {
                        room.addEnemy(enemyRegistry.get(c).create(x, y, currentLevel));
                    }
                }
                y++;
            }
        } catch (IOException e) {
            System.err.println("Critical error reading map: " + filepath + ". Loading fallback room.");
            for (int x = 0; x < ROOM_W; x++) {
                for (int y = 0; y < ROOM_H; y++) {
                    boolean isWall = (x == 0 || x == ROOM_W - 1 || y == 0 || y == ROOM_H - 1);
                    room.setTile(x, y, new Tile(!isWall));
                }
            }
        }
        return room;
    }

    private void addNeighborsToPoints(int x, int y, List<Point> pointsList, Room[][] dungeonMap) {
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < GRID_SIZE_X && ny >= 0 && ny < GRID_SIZE_Y && dungeonMap[nx][ny] == null) {
                Point p = new Point(nx, ny);
                if (!pointsList.contains(p)) pointsList.add(p);
            }
        }
    }

    private void punchAutomaticDoors(Room[][] dungeonMap) {
        for (int x = 0; x < GRID_SIZE_X; x++) {
            for (int y = 0; y < GRID_SIZE_Y; y++) {
                Room r = dungeonMap[x][y];
                if (r == null) continue;

                if (x + 1 < GRID_SIZE_X && dungeonMap[x+1][y] != null) {
                    r.setTile(ROOM_W - 1, ROOM_H / 2, new Tile(true));
                    dungeonMap[x+1][y].setTile(0, ROOM_H / 2, new Tile(true));
                }
                if (y + 1 < GRID_SIZE_Y && dungeonMap[x][y+1] != null) {
                    r.setTile(ROOM_W / 2, ROOM_H - 1, new Tile(true));
                    dungeonMap[x][y+1].setTile(ROOM_W / 2, 0, new Tile(true));
                }
            }
        }
    }

    private static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return p.x == x && p.y == y;
        }
    }

    private void loadAvailableRoomFiles() {
        normalRoomFiles = new ArrayList<>();
        File folder = new File("code/assets/rooms/");
    
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.startsWith("room_") && name.endsWith(".txt"));
        
            if (files != null && files.length > 0) {
                for (File file : files) {
                    normalRoomFiles.add("code/assets/rooms/" + file.getName());
                }
            }
            System.out.println("Loaded " + normalRoomFiles.size() + " room designs.");
            return;
        }
    System.err.println("Warning: No normal room files found! Falling back to default.");
    normalRoomFiles.add("code/assets/rooms/room_1.txt");
    }
}