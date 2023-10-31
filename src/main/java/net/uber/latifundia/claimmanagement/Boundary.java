package net.uber.latifundia.claimmanagement;

import java.awt.*;

public class Boundary {
    public int left, right, top, bottom;

    public Boundary(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public boolean contains(Point point) {
        return point.x >= left && point.x <= right && point.y >= bottom && point.y <= top;
    }

    public Boundary[] subdivide() {
        int width = (right - left) / 2;
        int height = (top - bottom) / 2;

        Boundary[] subBoundaries = new Boundary[4];
        subBoundaries[0] = new Boundary(left, left + width, top, top - height); // Top left
        subBoundaries[1] = new Boundary(left + width, right, top, top - height); // Top right
        subBoundaries[2] = new Boundary(left, left + width, top - height, bottom); // Bottom left
        subBoundaries[3] = new Boundary(left + width, right, top - height, bottom); // Bottom right

        return subBoundaries;
    }

}

