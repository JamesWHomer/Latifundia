package net.uber.latifundia.citystates;

import net.uber.latifundia.Latifundia;
import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
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
    private final Map<UUID, Relation> relationMap = new HashMap<>();

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
        this.worldClaimMap.put(creator.getWorld().getName(), claimList);
        this.memberList.put(creator.getUniqueId(), Rank.LEADER);
    }

    public String getName() {
        return this.name;
    }

    public int getPopulation() {
        return memberList.size();
    }

    public Rank getRank(Player player) {
        return this.memberList.get(player.getUniqueId());
    }

    public Set<UUID> getMembers() {
        return memberList.keySet();
    }

    public UUID getCityStateUUID() {
        return this.cityStateUUID;
    }

    public void removeMember(UUID player) {
        memberList.remove(player);
    }

    public void deleteSelf() {

        this.unclaimAllChunks();

    }

    public Relation getRelation(UUID cityStateUUID) {

        if (relationMap.containsKey(cityStateUUID)) {
            return relationMap.get(cityStateUUID);
        }

        return Relation.NEUTRAL;

    }

    public boolean ownsChunk(Chunk chunk) {
        Point point = new Point(chunk.getX(), chunk.getZ());
        String world = chunk.getWorld().getName();
        if (!worldClaimMap.containsKey(world)) return false;
        List<Point> pointList = worldClaimMap.get(world);
        return pointList.contains(point);
    }

    public void unclaimAllChunks() {

        for (String world : worldClaimMap.keySet()) {

            World bukkitWorld = Bukkit.getWorld(world);

            if (bukkitWorld != null) {
                List<Point> pointList = worldClaimMap.get(world);

                WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
                WorldTree worldTree = worldTreeManager.getWorldTree(bukkitWorld);

                for (Point chunkPoint : pointList) {
                    worldTree.removeClaim(chunkPoint);
                }
            }

        }

        worldClaimMap.clear();

    }

    public boolean claimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        List<Point> pointList = worldClaimMap.get(chunk.getWorld().getName());
        pointList.add(point);
        return worldTree.insertClaim(point, this.cityStateUUID);
    }

    public boolean unclaimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        List<Point> pointList = worldClaimMap.get(chunk.getWorld().getName());
        pointList.remove(point);
        return worldTree.removeClaim(point);
    }

    public enum Rank {
        LEADER,
        COLEADER,
        ELDER,
        CITIZEN
    }

}
