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

        if (!bounds.contains(point)) throw new IllegalStateException("Point is not within boundary");

        Boundary[] subs = bounds.subdivide();

        if (quadNodes[0] != null && subs[0].contains(point)) {
            boolean rembool = quadNodes[0].remove(point);
            if (rembool) {
                //Child was deleted
                quadNodes[0] = null;
                return hasNoChildren();
            } else {
                return false;
            }
        }
        if (quadNodes[1] != null && subs[1].contains(point)) {
            boolean rembool = quadNodes[1].remove(point);
            if (rembool) {
                //Child was deleted
                quadNodes[1] = null;
                return hasNoChildren();
            } else {
                return false;
            }
        }
        if (quadNodes[2] != null && subs[2].contains(point)) {
            boolean rembool = quadNodes[2].remove(point);
            if (rembool) {
                //Child was deleted
                quadNodes[2] = null;
                return hasNoChildren();
            } else {
                return false;
            }
        }
        if (quadNodes[3] != null && subs[3].contains(point)) {
            boolean rembool = quadNodes[3].remove(point);
            if (rembool) {
                //Child was deleted
                quadNodes[3] = null;
                return hasNoChildren();
            } else {
                return false;
            }
        }

        return false;

    }

    public boolean hasNoChildren() {

        if (quadNodes == null) {
            return true;
        }
        for (QuadTree node : quadNodes) {
            if (node != null) {
                return false;
            }
        }
        return true;

    }



    public UUID query(Point point) {

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