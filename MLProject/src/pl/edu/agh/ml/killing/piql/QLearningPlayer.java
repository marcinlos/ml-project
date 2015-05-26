package pl.edu.agh.ml.killing.piql;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.runner.Player;
import pl.edu.agh.ml.killing.state.StateInfo;
import algorithms.QLearningSelector;

import com.google.common.collect.ImmutableSet;

import environment.IState;

public class QLearningPlayer<T> implements Player {

    private final QLearningConfig config;
    private double epsilon;

    private final PiqleEnvironment<T> environment;
    private final QLearningSelector selector;

    private StateInfo prevState = null;
    private Action prevAction = null;

    public QLearningPlayer(QLearningConfig config, GameConfig gameConfig,
            Function<StateInfo, T> stateConverter) {
        this.config = checkNotNull(config);
        this.epsilon = config.initEpsilon();
        this.selector = config.newSelector();
        this.environment = new PiqleEnvironment<>(gameConfig, stateConverter);
    }

    @Override
    public Action chooseAction(StateInfo state, ImmutableSet<Action> availableActions) {
        learn(state);
        prevState = state;
        IState pState = new PiqleState<T>(environment, state);
        PiqleAction pAction = (PiqleAction) selector.getChoice(pState.getActionList());
        prevAction = pAction.action();
        return pAction.action();
    }

    private void learn(StateInfo state) {
        if (prevState != null) {
            double reward = environment.reward(prevState, state);
            PiqleState<T> before = new PiqleState<>(environment, prevState);
            PiqleState<T> after = new PiqleState<>(environment, state);
            selector.learn(before, after, new PiqleAction(prevAction), reward);
        }
    }

    @Override
    public void gameFinished(StateInfo state, Optional<Result> result) {
        epsilon *= config.epsilonDecay();
        selector.setEpsilon(epsilon);
    }

}
