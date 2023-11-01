package net.uber.latifundia.claimmanagement;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
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

    public void loadAllWorldTreesAsync(Runnable callback) {
        File dataFolder = new File(plugin.getDataFolder(), "worldTrees");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File[] worldFiles = dataFolder.listFiles((dir, name) -> name.endsWith(".ser"));
        if (worldFiles == null || worldFiles.length == 0) {
            callback.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(worldFiles.length);
        for (File worldFile : worldFiles) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    String worldName = worldFile.getName().replace(".ser", "");
                    WorldTree worldTree = loadWorldTree(worldFile.getAbsolutePath());
                    worldTrees.put(worldName, worldTree);
                } catch (IOException | ClassNotFoundException e) {
                    plugin.getLogger().severe("Could not load WorldTree asynchronously for world: " + worldFile.getName());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                latch.await();
                plugin.getServer().getScheduler().runTask(plugin, callback);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
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

    public void saveAllWorldTreesAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            saveAllWorldTrees();
        });
    }


    public void saveWorldTree(WorldTree worldTree, String filepath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(Files.newOutputStream(Paths.get(filepath))))) {
            out.writeObject(worldTree);
        }
    }

    public void saveWorldTreeAsync(WorldTree worldTree, String filepath) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                saveWorldTree(worldTree, filepath);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save WorldTree asynchronously");
                e.printStackTrace();
            }
        });
    }


    public WorldTree loadWorldTree(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(Files.newInputStream(Paths.get(filepath))))) {
            return (WorldTree) in.readObject();
        }
    }

    public void loadWorldTreeAsync(String filepath, Consumer<WorldTree> callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                WorldTree worldTree = loadWorldTree(filepath);
                plugin.getServer().getScheduler().runTask(plugin, () -> callback.accept(worldTree));
            } catch (IOException | ClassNotFoundException e) {
                plugin.getLogger().severe("Could not load WorldTree asynchronously");
                e.printStackTrace();
            }
        });
    }


}
