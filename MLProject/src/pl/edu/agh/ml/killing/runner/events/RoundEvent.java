package pl.edu.agh.ml.killing.runner.events;

import static com.google.common.base.Preconditions.checkNotNull;
import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.state.StateInfo;

public class RoundEvent {

    private final Action action;
    private final StateInfo state;

    public RoundEvent(Action action, StateInfo state) {
        this.action = checkNotNull(action);
        this.state = checkNotNull(state);
    }

    public Action action() {
        return action;
    }

    public StateInfo state() {
        return state;
    }

}
