package net.uber.latifundia.claimmanagement;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;

public class WorldTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private QuadTree headNode;
    private String world;

    /**
     * Initialize a new world tree
     * @param world Specify world
     */
    public WorldTree(String world) {

        this.headNode = new QuadTree(new Boundary(-2097152, 2097152, 2097152, -2097152), 0);
        this.world = world;

    }

    public WorldTree() {

    }

    public boolean insertClaim(Point point, UUID owner) {

        return headNode.insert(point, owner);

    }

    public UUID queryClaim(Point point) {

        return headNode.query(point);

    }

    public boolean removeClaim(Point point) {

        return headNode.remove(point);

    }

}
