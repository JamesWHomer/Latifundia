package net.uber.latifundia.claimmanagement;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
            this.createWorldTree(world);
        }
        return worldTrees.get(world.getName());

    }

    public void createWorldTree(World world) {
        worldTrees.put(world.getName(), new WorldTree());
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

    public void saveWorldTree(WorldTree worldTree, String filepath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(Files.newOutputStream(Paths.get(filepath))))) {
            out.writeObject(worldTree);
        }
    }

    public WorldTree loadWorldTree(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(Files.newInputStream(Paths.get(filepath))))) {
            return (WorldTree) in.readObject();
        }
    }

}
