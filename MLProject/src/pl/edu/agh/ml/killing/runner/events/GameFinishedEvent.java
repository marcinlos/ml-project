package pl.edu.agh.ml.killing.runner.events;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import pl.edu.agh.ml.killing.core.Result;

public class GameFinishedEvent {

    private final int rounds;
    private final Optional<Result> result;

    public GameFinishedEvent(int rounds, Optional<Result> result) {
        checkArgument(rounds > 0);
        this.rounds = rounds;
        this.result = checkNotNull(result);
    }

    public Optional<Result> result() {
        return result;
    }

    public int rounds() {
        return rounds;
    }

}
