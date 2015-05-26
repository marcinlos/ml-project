package pl.edu.agh.ml.killing.qlearning;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;

import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.game.GameEngine;
import pl.edu.agh.ml.killing.state.StateInfo;
import environment.AbstractEnvironmentSingle;
import environment.ActionList;
import environment.IAction;
import environment.IState;

public class PiqleEnvironment<T> extends AbstractEnvironmentSingle {

    private final GameConfig config;
    private final Function<StateInfo, T> mapper;

    public PiqleEnvironment(GameConfig config, Function<StateInfo, T> mapper) {
        this.config = checkNotNull(config);
        this.mapper = checkNotNull(mapper);
    }

    public T map(StateInfo state) {
        return mapper.apply(state);
    }

    private StateInfo info(IState s) {
        @SuppressWarnings("unchecked")
        PiqleState<T> state = (PiqleState<T>) s;
        return state.state();
    }

    private GameEngine load(IState s) {
        return GameEngine.load(config, info(s));
    }

    @Override
    public ActionList getActionList(IState s) {
        GameEngine game = load(s);
        ActionList actionList = new ActionList(s);
        game.availableActions().forEach(a -> actionList.add(new PiqleAction(a)));
        return actionList;
    }

    @Override
    public IState successorState(IState s, IAction a) {
        PiqleAction pqAction = (PiqleAction) a;
        GameEngine game = load(s);
        game.playRound(pqAction.action());
        return state(game);
    }

    private IState state(GameEngine game) {
        return new PiqleState<>(this, game.snapshot());
    }

    @Override
    public IState defaultInitialState() {
        GameEngine game = GameEngine.create(config);
        return state(game);
    }

    @Override
    public double getReward(IState s1, IState s2, IAction a) {
        return reward(info(s1), info(s2));
    }

    public double reward(StateInfo s1, StateInfo s2) {
        if (s2.player().isDead()) {
            return 0;
        }
        int dHp = s2.player().hp() - s1.player().hp();
        int dCount = s2.enemies().size() - s1.enemies().size();
        return config.playerHp() + dHp + dCount;
    }

    @Override
    public boolean isFinal(IState s) {
        return info(s).finished();
    }

    @Override
    public int whoWins(IState s) {
        switch (info(s).result().get()) {
        case LOST:
            return 1;
        case WON:
            return -1;
        default:
            throw new AssertionError();
        }
    }

}
