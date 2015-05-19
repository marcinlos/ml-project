package pl.edu.agh.ml.killing.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

public class State {

    private final Battleground battleground;

    private final Entity player;

    public State(Battleground battleground, Entity player) {
        this.battleground = checkNotNull(battleground);
        this.player = checkNotNull(player);
    }

    public Battleground battleground() {
        return battleground;
    }

    public Entity player() {
        return player;
    }

    public Optional<Result> result() {
        if (player.isDead()) {
            return Optional.of(Result.LOST);
        } else if (battleground.entities().size() == 1) {
            return Optional.of(Result.WON);
        } else {
            return Optional.empty();
        }
    }

    public boolean finished() {
        return result().isPresent();
    }

}
