package pl.edu.agh.ml.killing.piql;

import static com.google.common.base.Preconditions.checkNotNull;
import pl.edu.agh.ml.killing.core.Action;
import environment.IAction;

public class PiqleAction implements IAction {
    
    private final Action action;
    
    public PiqleAction(Action action) {
        this.action = checkNotNull(action);
    }

    @Override
    public Object copy() {
        return new PiqleAction(action);
    }

    public Action action() {
        return action;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PiqleAction) {
            PiqleAction other = (PiqleAction) obj;
            return action.equals(other.action);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return action.hashCode();
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
