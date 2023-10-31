package net.uber.latifundia.claimmanagement;

import org.bukkit.Bukkit;

import java.awt.*;
import java.util.UUID;

public class QuadTree {

    int depth;
    QuadTree[] quadNodes;
    Boundary bounds;


    public QuadTree(Boundary bounds, int depth) {

        this.bounds = bounds;
        this.depth = depth;
        quadNodes = new QuadTree[4];

    }

    public boolean insert(Point point, UUID owner) {

        Bukkit.getConsoleSender().sendMessage("Depth: " + depth + ", Width: " + (bounds.top - bounds.bottom));

        if (!bounds.contains(point)) return false;

        Boundary[] subs = bounds.subdivide();

        if (subs[0].contains(point)) {
            return insertIndex(0, point, owner, subs[0]);
        }
        if (subs[1].contains(point)) {
            return insertIndex(1, point, owner, subs[1]);
        }
        if (subs[2].contains(point)) {
            return insertIndex(2, point, owner, subs[2]);
        }
        if (subs[3].contains(point)) {
            return insertIndex(3, point, owner, subs[3]);
        }

        return false;

    }

    private boolean insertIndex(int index, Point point, UUID owner, Boundary subbound) {

        if ((bounds.top - bounds.bottom) == 2) {
            // Leaf Node

            quadNodes[index] = new QuadLeaf(point, owner, depth);
            return true;

        } else {

            if (quadNodes[index] == null) {
                quadNodes[index] = new QuadTree(subbound, depth + 1);
            }

            return quadNodes[index].insert(point, owner);

        }

    }

    public boolean remove(Point point) {
        // Check if point is out of bounds
        if (!bounds.contains(point)) return false;

        // If this is a leaf node, remove the point directly
        if ((bounds.top - bounds.bottom) <= 2) {
            boolean removed = false;
            for (int i = 0; i < quadNodes.length; i++) {
                if (quadNodes[i] != null && quadNodes[i] instanceof QuadLeaf) {
                    QuadLeaf leaf = (QuadLeaf) quadNodes[i];
                    if (leaf.point.equals(point)) {
                        quadNodes[i] = null;
                        removed = true;
                        break;
                    }
                }
            }
            return removed;
        }

        // Not a leaf node, delegate to the appropriate child node
        Boundary[] subs = bounds.subdivide();
        for (int i = 0; i < quadNodes.length; i++) {
            if (quadNodes[i] != null && subs[i].contains(point)) {
                boolean removed = quadNodes[i].remove(point);
                if (removed) {
                    cleanup();
                }
                return removed;
            }
        }

        return false;
    }

    private void cleanup() {
        int nonNullChildren = 0;
        QuadTree singleChild = null;

        for (QuadTree child : quadNodes) {
            if (child != null) {
                nonNullChildren++;
                singleChild = child;
            }
        }

        if (nonNullChildren == 0) {
            // All child nodes are null, this node is now effectively empty
            for (int i = 0; i < quadNodes.length; i++) {
                quadNodes[i] = null;
            }
        } else if (nonNullChildren == 1 && singleChild instanceof QuadLeaf) {
            // Only one child node and it is a leaf, replace this node with the leaf
            this.bounds = singleChild.bounds;
            this.quadNodes = singleChild.quadNodes;
        } else {
            // More than one child node, or single child is not a leaf, recursively cleanup child nodes
            for (QuadTree child : quadNodes) {
                if (child != null) {
                    child.cleanup();
                }
            }
        }
    }



    public UUID query(Point point) {

        Bukkit.getConsoleSender().sendMessage("Depth: " + depth + ", Width: " + (bounds.top - bounds.bottom));

        if (!bounds.contains(point)) throw new IllegalStateException("Point is not within boundary");

        if ((bounds.top - bounds.bottom) <= 2) {
            // Leaf Node

            Boundary[] subs = bounds.subdivide();

            if (quadNodes[0] != null && subs[0].contains(point)) {
                return quadNodes[0].query(point);
            }
            if (quadNodes[1] != null && subs[1].contains(point)) {
                return quadNodes[1].query(point);
            }
            if (quadNodes[2] != null && subs[2].contains(point)) {
                return quadNodes[2].query(point);
            }
            if (quadNodes[3] != null && subs[3].contains(point)) {
                return quadNodes[3].query(point);
            }

            return null;

        }

        Boundary[] subs = bounds.subdivide();

        if (quadNodes[0] != null && subs[0].contains(point)) {
            return quadNodes[0].query(point);
        }
        if (quadNodes[1] != null && subs[1].contains(point)) {
            return quadNodes[1].query(point);
        }
        if (quadNodes[2] != null && subs[2].contains(point)) {
            return quadNodes[2].query(point);
        }
        if (quadNodes[3] != null && subs[3].contains(point)) {
            return quadNodes[3].query(point);
        }

        return null;

    }

}
