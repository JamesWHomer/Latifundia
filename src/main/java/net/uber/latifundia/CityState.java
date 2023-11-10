package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class CityState implements Serializable {
    private static final long serialVersionUID = 1L;

    // Planning on implementing Empires but not yet.

    private final String name;
    private final UUID cityStateUUID;
    private final Map<UUID, Rank> memberList = new HashMap<>();
    private final Map<String, List<Point>> worldClaimMap = new HashMap<>();

    /**
     * Creates new city state from scratch.
     * @param name Creates name of CityState
     * @param creator Clarifies who is Praetor
     */
    public CityState(UUID cuuid, String name, Player creator) {
        this.name = name;
        this.cityStateUUID = cuuid;
        Point chunkPoint = new Point(creator.getLocation().getChunk().getX(), creator.getLocation().getChunk().getZ());
        List<Point> claimList = new ArrayList<>();
        claimList.add(chunkPoint);
        this.worldClaimMap.put(creator.getWorld().toString(), claimList);
        this.memberList.put(creator.getUniqueId(), Rank.LEADER);
    }

    public String getName() {
        return this.name;
    }

    public Set<UUID> getMembers() {
        return memberList.keySet();
    }

    public UUID getCityStateUUID() {
        return this.cityStateUUID;
    }

    public void deleteSelf() {

        this.unclaimAllChunks();

    }

    public boolean ownsChunk(Chunk chunk) {
        Point point = new Point(chunk.getX(), chunk.getZ());
        String world = chunk.getWorld().toString();
        if (!worldClaimMap.containsKey(world)) return false;
        List<Point> pointList = worldClaimMap.get(world);
        return pointList.contains(point);
    }

    public void unclaimAllChunks() {

        for (String world : worldClaimMap.keySet()) {

            List<Point> pointList = worldClaimMap.get(world);

            WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
            WorldTree worldTree = worldTreeManager.getWorldTree(Bukkit.getWorld(world));

            for (Point chunkPoint : pointList) {
                worldTree.removeClaim(chunkPoint);
            }

        }

        worldClaimMap.clear();

    }

    public boolean claimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        List<Point> pointList = worldClaimMap.get(chunk.getWorld().toString());
        pointList.add(point);
        return worldTree.insertClaim(point, this.cityStateUUID);
    }

    public boolean unclaimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        List<Point> pointList = worldClaimMap.get(chunk.getWorld().toString());
        pointList.remove(point);
        return worldTree.removeClaim(point);
    }

    enum Rank {
        LEADER,
        COLEADER,
        ELDER,
        CITIZEN
    }

}
