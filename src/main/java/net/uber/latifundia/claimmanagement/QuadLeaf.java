package net.uber.latifundia.claimmanagement;

import org.bukkit.Bukkit;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;

public class QuadLeaf extends QuadTree implements Serializable {
    private static final long serialVersionUID = 1L;

    Point point;
    UUID owner;

    public QuadLeaf(Point point, UUID owner, int depth) {
        super(new Boundary(point.x, point.x + 1, point.y, point.y - 1), depth);
        this.point = point;
        this.owner = owner;
    }

    @Override
    public UUID query(Point point) {
        return this.owner;
    }

    @Override
    public boolean remove(Point point) {
        return true;
    }

    public boolean isPoint(Point point) {
        return this.point.equals(point);
    }

}
