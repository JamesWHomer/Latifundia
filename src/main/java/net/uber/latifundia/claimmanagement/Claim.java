package net.uber.latifundia.claimmanagement;

import org.bukkit.Location;

import java.util.UUID;

public class Claim {
    private final UUID owner;
    private final int chunkX;
    private final int chunkZ;

    public Claim(UUID owner, Location location) {
        this.owner = owner;
        this.chunkX = location.getChunk().getX();
        this.chunkZ = location.getChunk().getZ();
    }
    public Claim(UUID owner, int chunkX, int chunkZ) {
        this.owner = owner;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
