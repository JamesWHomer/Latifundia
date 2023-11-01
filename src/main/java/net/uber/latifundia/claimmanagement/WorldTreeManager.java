package net.uber.latifundia.claimmanagement;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WorldTreeManager {

    private final JavaPlugin plugin;
    private final Map<String, WorldTree> worldTrees = new HashMap<>();

    public WorldTreeManager(JavaPlugin plugin) {

        this.plugin = plugin;
        loadAllWorldTrees();

    }

    private void loadAllWorldTrees() {
        File dataFolder = new File(plugin.getDataFolder(), "worldTrees");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        for (World world : plugin.getServer().getWorlds()) {
            File worldFile = new File(dataFolder, world.getName() + ".ser");
            if (worldFile.exists()) {
                try {
                    WorldTree worldTree = loadWorldTree(worldFile.getAbsolutePath());
                    worldTrees.put(world.getName(), worldTree);
                } catch (IOException | ClassNotFoundException e) {
                    plugin.getLogger().severe("Could not load WorldTree for world " + world.getName());
                    e.printStackTrace();
                }
            }
        }
    }


    public WorldTree getWorldTree(World world) {

        if (!worldTrees.containsKey(world.getName())) {
            worldTrees.put(world.getName(), new WorldTree(world.getName()));
        }
        return worldTrees.get(world.getName());

    }

    public void addWorldTree(World world, WorldTree worldTree) {
        worldTrees.put(world.getName(), worldTree);
    }

    public void removeWorldTree(World world) {
        worldTrees.remove(world.getName());
    }

    public void saveAllWorldTrees() {
        File dataFolder = new File(plugin.getDataFolder(), "worldTrees");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        for (Map.Entry<String, WorldTree> entry : worldTrees.entrySet()) {
            try {
                File worldFile = new File(dataFolder, entry.getKey() + ".ser");
                saveWorldTree(entry.getValue(), worldFile.getAbsolutePath());
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save WorldTree for world " + entry.getKey());
                e.printStackTrace();
            }
        }
    }

    public void saveWorldTree(WorldTree worldTree, String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(filename)))) {
            out.writeObject(worldTree);
        }
    }

    public WorldTree loadWorldTree(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(filename)))) {
            return (WorldTree) in.readObject();
        }
    }

}
