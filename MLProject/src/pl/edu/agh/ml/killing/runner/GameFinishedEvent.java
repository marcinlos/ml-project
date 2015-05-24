package pl.edu.agh.ml.killing.runner;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.game.GameEngine;

public class GameFinishedEvent {

    private final Optional<Result> result;

    public GameFinishedEvent(Optional<Result> result) {
        this.result = checkNotNull(result);
    }

    public Optional<Result> result() {
        return result;
    }

    public static GameFinishedEvent from(GameEngine game) {
        return new GameFinishedEvent(game.result());
    }

}
