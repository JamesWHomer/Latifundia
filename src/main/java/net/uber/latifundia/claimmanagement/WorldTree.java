package net.uber.latifundia.claimmanagement;

import java.awt.*;
import java.util.UUID;

public class WorldTree {

    private QuadTree headNode;

    /**
     * Initialize a new world tree
     * @param world Specify world
     */
    public WorldTree(String world) {

        this.headNode = new QuadTree(new Boundary(-2097152, 2097152, 2097152, -2097152), 0);

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
