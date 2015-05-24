package pl.edu.agh.ml.killing.piql;

import static com.google.common.base.Preconditions.checkNotNull;
import pl.edu.agh.ml.killing.state.StateInfo;
import environment.AbstractState;
import environment.IState;

public class PiqleState<T> extends AbstractState {

    private final PiqleEnvironment<T> env;
    private final StateInfo fullState;
    private final T state;

    public PiqleState(PiqleEnvironment<T> env, StateInfo state) {
        super(env);
        this.env = checkNotNull(env);
        this.fullState = checkNotNull(state);
        this.state = env.map(state);
    }

    @Override
    public IState copy() {
        return new PiqleState<>(env, fullState);
    }

    public StateInfo state() {
        return fullState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PiqleState) {
            @SuppressWarnings("unchecked")
            PiqleState<T> other = (PiqleState<T>) obj;
            return state.equals(other.state);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public int nnCodingSize() {
        throw new UnsupportedOperationException("nnCodingSize not implemented");
    }

    @Override
    public double[] nnCoding() {
        throw new UnsupportedOperationException("nnCodingSize not implemented");
    }

}
