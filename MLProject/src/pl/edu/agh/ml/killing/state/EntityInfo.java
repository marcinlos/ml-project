package pl.edu.agh.ml.killing.state;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import pl.edu.agh.ml.killing.core.Entity;
import pl.edu.agh.ml.killing.core.Position;
import pl.edu.agh.ml.killing.core.Side;

public final class EntityInfo {

    private final Side side;

    private final int hp;

    private final Position position;

    public EntityInfo(Side side, int hp, Position position) {
        checkArgument(hp >= 0, "Negative HP");
        this.side = checkNotNull(side);
        this.hp = hp;
        this.position = checkNotNull(position);
    }

    public static EntityInfo from(Entity entity) {
        return new EntityInfo(entity.side(), entity.hp(), entity.position());
    }

    public Side side() {
        return side;
    }

    public int hp() {
        return hp;
    }

    public Position position() {
        return position;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityInfo) {
            EntityInfo other = (EntityInfo) obj;
            return side == other.side && hp == other.hp && position.equals(other.position);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(side, hp, position);
    }
}
