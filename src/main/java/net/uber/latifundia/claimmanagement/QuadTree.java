package net.uber.latifundia.claimmanagement;

import org.bukkit.Bukkit;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;

public class QuadTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private int depth;
    private QuadTree[] quadNodes;
    private Boundary bounds;

    public QuadTree(Boundary bounds, int depth) {
        this.bounds = bounds;
        this.depth = depth;
        this.quadNodes = new QuadTree[4];
    }

    public boolean insert(Point point, UUID owner) {
        if (!bounds.contains(point)) return false;

        if (isLeafNode()) {
            quadNodes[getIndex(point)] = new QuadLeaf(point, owner, depth);
            return true;
        }

        return insertAtSubNode(point, owner);
    }

    private boolean insertAtSubNode(Point point, UUID owner) {
        int index = getIndex(point);
        Boundary subBoundary = bounds.subdivide()[index];

        if (quadNodes[index] == null) {
            quadNodes[index] = new QuadTree(subBoundary, depth + 1);
        }
        return quadNodes[index].insert(point, owner);
    }

    public boolean remove(Point point) {
        if (!bounds.contains(point)) throw new IllegalStateException("Point is not within boundary");

        int index = getIndex(point);
        QuadTree subNode = quadNodes[index];
        if (subNode != null && subNode.remove(point)) {
            quadNodes[index] = null;
            return hasNoChildren();
        }

        return false;
    }

    public boolean hasNoChildren() {
        for (QuadTree node : quadNodes) {
            if (node != null) return false;
        }
        return true;
    }

    public UUID query(Point point) {
        if (!bounds.contains(point)) throw new IllegalStateException("Point is not within boundary");

        if (isLeafNode()) return queryInLeafNode(point);

        int index = getIndex(point);
        QuadTree subNode = quadNodes[index];
        return (subNode != null) ? subNode.query(point) : null;
    }

    private UUID queryInLeafNode(Point point) {
        for (QuadTree quadNode : quadNodes) {
            QuadLeaf subNode = (QuadLeaf) quadNode;
            if (subNode != null && subNode.isPoint(point)) {
                return subNode.query(point);
            }
        }
        return null;
    }

    private boolean isLeafNode() {
        return (bounds.top - bounds.bottom) <= 2;
    }

    private int getIndex(Point point) {
        Boundary[] subBoundaries = bounds.subdivide();
        for (int i = 0; i < subBoundaries.length; i++) {
            if (subBoundaries[i].contains(point)) return i;
        }
        throw new IllegalStateException("Point is not within any sub-boundary");
    }
}

