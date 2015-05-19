package pl.edu.agh.ml.killing.core;

import static com.google.common.base.Preconditions.checkArgument;

public class MapExtent {

    private final int width;

    private final int height;

    public MapExtent(int width, int height) {
        checkArgument(width > 0);
        checkArgument(height > 0);
        this.width = width;
        this.height = height;
    }

    public boolean inRange(Position pos) {
        return pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MapExtent) {
            MapExtent other = (MapExtent) obj;
            return width == other.width && height == other.height;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return width + (1 << 12 - 1) * height;
    }

    @Override
    public String toString() {
        return String.format("Battleground(%dx%d)", width, height);
    }
}
