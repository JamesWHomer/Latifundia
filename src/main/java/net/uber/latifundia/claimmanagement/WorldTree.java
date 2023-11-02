package net.uber.latifundia.claimmanagement;

import org.bukkit.Bukkit;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;

public class WorldTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private QuadTree headNode;

    /**
     * Initialize a new world tree
     */
    public WorldTree() {

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
