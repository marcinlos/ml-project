package pl.edu.agh.ml.killing.runner;

import java.util.Optional;

import com.google.common.collect.ImmutableSet;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.state.StateInfo;

public interface Player {

    Action chooseAction(StateInfo state, ImmutableSet<Action> availableActions);

    void gameFinished(Optional<Result> result);

}
