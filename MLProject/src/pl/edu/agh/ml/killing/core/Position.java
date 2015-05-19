package pl.edu.agh.ml.killing.core;

import static com.google.common.base.Preconditions.checkNotNull;

public class Position {

    public final int x;

    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return x + ((1 << 12) - 1) * y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position other = (Position) obj;
            return x == other.x && y == other.y;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public Position move(Direction dir, int steps) {
        checkNotNull(dir);
        switch (dir) {
        case UP:
            return new Position(x, y + steps);
        case DOWN:
            return new Position(x, y - steps);
        case LEFT:
            return new Position(x - steps, y);
        case RIGHT:
            return new Position(x + steps, y);
        default:
            throw new AssertionError("Impossible");
        }
    }

    public Position move(Direction dir) {
        return move(dir, 1);
    }
}