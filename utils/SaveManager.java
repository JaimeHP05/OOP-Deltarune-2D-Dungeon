package code.utils;

import code.world.Grid;
import code.model.entities.Player;
import java.io.*;

public class SaveManager {
    private static final String FILE_NAME = "savegame.sav";

    public static void saveGame(Grid grid, Player player) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(grid);
            out.writeObject(player);
            System.out.println("Game saved successfully!");
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    public static Object[] loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            Grid grid = (Grid) in.readObject();
            Player player = (Player) in.readObject();
            System.out.println("Game loaded!");
            return new Object[]{grid, player};
        } catch (Exception e) {
            System.out.println("No previous save game found.");
            return null;
        }
    }
}