package pl.edu.agh.ml.killing.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class Battleground {

    private final MapExtent extent;

    private final ImmutableMap<Position, Entity> entities;

    public Battleground(MapExtent extent, ImmutableMap<Position, Entity> entities) {
        this.extent = checkNotNull(extent);
        this.entities = checkNotNull(entities);
    }

    public MapExtent extent() {
        return extent;
    }

    public ImmutableMap<Position, Entity> entities() {
        return entities;
    }

    public Optional<Entity> get(Position p) {
        return Optional.ofNullable(entities.get(p));
    }

    public boolean isEmpty(Position pos) {
        checkArgument(extent.inRange(pos), "Invalid position");
        return !get(pos).isPresent();
    }

    public Set<Direction> validMoves(Position pos) {
        checkArgument(extent.inRange(pos), "Invalid initial position");

        ImmutableSet.Builder<Direction> allowed = ImmutableSet.builder();
        for (Direction d : Direction.values()) {
            Position p = pos.move(d);
            if (extent.inRange(p) && isEmpty(p)) {
                allowed.add(d);
            }
        }
        return allowed.build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(extent, entities);
    }

}
