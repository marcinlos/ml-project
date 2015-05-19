package pl.edu.agh.ml.killing.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Optional;

public class Entity {

    private final int hp;

    private final Position position;

    public Entity(int hp, Position position) {
        checkArgument(hp > 0, "Nonpositive initial HP");
        this.hp = hp;
        this.position = checkNotNull(position);
    }

    public Position position() {
        return position;
    }

    public int hp() {
        return hp;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public Optional<Entity> damage(int dmg) {
        checkArgument(dmg > 0, "Nonpositive damage");
        int newHp = hp - dmg;
        if (newHp > 0) {
            return Optional.of(new Entity(newHp, position));
        } else {
            return Optional.empty();
        }
    }

    public Entity move(Position dest) {
        return new Entity(hp, dest);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entity) {
            Entity other = (Entity) obj;
            return hp == other.hp && position.equals(other.position);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(hp, position);
    }

    @Override
    public String toString() {
        return String.format("Entity (at %s, HP: %d)", position, hp);
    }

}
