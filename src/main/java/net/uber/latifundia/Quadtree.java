package net.uber.latifundia;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Quadtree {
    private static final int MAX_CHUNKS = 4;
    private final int level;
    private final List<Claim> claims;
    private final Rectangle bounds;
    private Quadtree[] nodes;

    public Quadtree(int level, Rectangle bounds) {
        this.level = level;
        this.claims = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = null;
    }

    public void insert(Claim claim) {
        if (!bounds.contains(claim.getChunkX(), claim.getChunkZ())) {
            return;
        }

        if (claims.size() < MAX_CHUNKS) {
            claims.add(claim);
            return;
        }

        if (nodes == null) {
            split();
        }

        for (Quadtree node : nodes) {
            node.insert(claim);
        }
    }

    public Claim getOwner(int chunkX, int chunkZ) {
        if (!bounds.contains(chunkX, chunkZ)) {
            return null;
        }

        for (Claim claim : claims) {
            if (claim.getChunkX() == chunkX && claim.getChunkZ() == chunkZ) {
                return claim;
            }
        }

        if (nodes != null) {
            for (Quadtree node : nodes) {
                Claim owner = node.getOwner(chunkX, chunkZ);
                if (owner != null) {
                    return owner;
                }
            }
        }

        return null;
    }

    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        nodes = new Quadtree[4];
        nodes[0] = new Quadtree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[1] = new Quadtree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }
}
