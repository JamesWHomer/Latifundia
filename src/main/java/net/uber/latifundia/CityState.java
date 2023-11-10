package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class CityState implements Serializable {
    private static final long serialVersionUID = 1L;

    // Planning on implementing Empires but not yet.

    private String name;
    private UUID cityStateUUID;
    private Map<UUID, Rank> memberList = new HashMap<>();
    private List<Point> claims = new ArrayList<>();

    /**
     * Creates new city state from scratch.
     * @param name Creates name of CityState
     * @param creator Clarifies who is Praetor
     */
    public CityState(UUID cuuid, String name, Player creator) {
        this.name = name;
        this.cityStateUUID = cuuid;
        this.claims.add(new Point(creator.getLocation().getChunk().getX(), creator.getLocation().getChunk().getZ()));
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
        return claims.contains(point);
    }

    public void unclaimAllChunks() {

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());

        for (Point chunkPoint : this.claims) {
            worldTree.removeClaim(chunkPoint);
        }

        this.claims.clear();

    }

    public boolean claimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        claims.add(point);
        return worldTree.insertClaim(point, this.cityStateUUID);
    }

    public boolean unclaimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        claims.remove(point);
        return worldTree.removeClaim(point);
    }

    enum Rank {
        LEADER,
        COLEADER,
        ELDER,
        CITIZEN
    }

}
