package pl.edu.agh.ml.killing.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Entity {

    private final Side side;

    private int hp;

    private Position position;

    public Entity(Side side, int hp, Position position) {
        checkArgument(hp >= 0, "Negative initial HP");
        this.side = checkNotNull(side);
        this.hp = hp;
        this.position = checkNotNull(position);
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

    public void move(Position dest) {
        position = checkNotNull(dest);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    public void hurt(int dmg) {
        hp = Math.max(hp - dmg, 0);
    }

}
