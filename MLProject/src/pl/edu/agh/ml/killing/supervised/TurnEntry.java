package pl.edu.agh.ml.killing.supervised;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.core.Action;

public final class TurnEntry<T> {

    private final Optional<T> state;
    private final Optional<Action> action;

    private TurnEntry(Optional<T> state, Optional<Action> action) {
        this.state = state;
        this.action = action;
    }

    public Optional<T> state() {
        return state;
    }

    public Optional<Action> action() {
        return action;
    }

    public <S> TurnEntry<S> map(Function<? super T, ? extends S> f) {
        return new TurnEntry<>(state.map(f), action);
    }

    public TurnEntry<T> withAction(Action action) {
        return new TurnEntry<>(state, Optional.of(action));
    }

    public TurnEntry<T> withoutAction() {
        return new TurnEntry<>(state, Optional.empty());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TurnEntry) {
            TurnEntry<?> other = (TurnEntry<?>) obj;
            return state.equals(other.state) && action.equals(other.action);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, action);
    }

    private static final TurnEntry<Object> EMPTY = builder().build();

    @SuppressWarnings("unchecked")
    public static <T> TurnEntry<T> empty() {
        return (TurnEntry<T>) EMPTY;
    }

    public static <T> TurnEntry<T> of(T state, Action action) {
        return new Builder<T>()
                .withState(state)
                .withAction(action)
                .build();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private T state;
        private Action action;

        public Builder<T> withState(T state) {
            this.state = checkNotNull(state);
            return this;
        }

        public Builder<T> withAction(Action action) {
            this.action = checkNotNull(action);
            return this;
        }

        public TurnEntry<T> build() {
            return new TurnEntry<>(ofNullable(state), ofNullable(action));
        }
    }
}
