package net.uber.latifundia;

public class Claim {
    private final String owner;
    private final int chunkX;
    private final int chunkZ;

    public Claim(String owner, int chunkX, int chunkZ) {
        this.owner = owner;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public String getOwner() {
        return owner;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
