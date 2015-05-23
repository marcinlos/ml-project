package pl.edu.agh.ml.killing.game;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import pl.edu.agh.ml.killing.core.Direction;
import pl.edu.agh.ml.killing.core.Entity;
import pl.edu.agh.ml.killing.core.MapExtent;
import pl.edu.agh.ml.killing.core.Position;

import com.google.common.collect.ImmutableSet;

class Battlefield {

    private final MapExtent extent;

    private final Map<Position, Entity> entities = new HashMap<>();

    public Battlefield(MapExtent extent) {
        this.extent = checkNotNull(extent);
    }

    public void add(Iterable<Entity> entities) {
        entities.forEach(this::add);
    }

    public void add(Entity e) {
        Position pos = e.position();
        checkState(isEmpty(pos), "Duplicate entity at " + pos);
        entities.put(pos, e);
    }

    public MapExtent extent() {
        return extent;
    }

    public Optional<Entity> get(Position pos) {
        checkRange(pos);
        return Optional.ofNullable(entities.get(pos));
    }

    public Optional<Entity> remove(Position pos) {
        checkRange(pos);
        return Optional.ofNullable(entities.remove(pos));
    }

    public boolean isEmpty(Position pos) {
        return !get(pos).isPresent();
    }

    public int entityCount() {
        return entities.size();
    }

    public int freeFields() {
        return extent.area() - entityCount();
    }

    public void move(Entity entity, Position dest) {
        checkRange(dest);
        checkState(isEmpty(dest), "Cannot move to occupied position");

        Position old = entity.position();
        assert entities.get(old) == entity;

        entities.remove(old);
        entities.put(dest, entity);
        entity.move(dest);
    }

    public Set<Direction> validMoves(Position pos) {
        checkRange(pos);

        ImmutableSet.Builder<Direction> allowed = ImmutableSet.builder();
        for (Direction d : Direction.values()) {
            Position p = pos.move(d);
            if (extent.inRange(p) && isEmpty(p)) {
                allowed.add(d);
            }
        }
        return allowed.build();
    }

    private void checkRange(Position p) {
        checkArgument(extent.inRange(p), "Invalid position");
    }
}
