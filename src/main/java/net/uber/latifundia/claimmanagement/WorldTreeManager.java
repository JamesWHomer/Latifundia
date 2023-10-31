package net.uber.latifundia.claimmanagement;

import org.bukkit.World;
import java.util.HashMap;
import java.util.Map;

public class WorldTreeManager {
    private final Map<String, WorldTree> worldTrees = new HashMap<>();

    public WorldTree getWorldTree(World world) {
        return worldTrees.computeIfAbsent(world.getName(), WorldTree::new);
    }

    public void addWorldTree(World world, WorldTree worldTree) {
        worldTrees.put(world.getName(), worldTree);
    }

    public void removeWorldTree(World world) {
        worldTrees.remove(world.getName());
    }
}
