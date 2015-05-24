package pl.edu.agh.ml.killing.supervised;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.core.Action;

import com.google.common.collect.ImmutableList;

public final class GameFragment<T> {

    private final ImmutableList<TurnEntry<T>> turns;
    private final Optional<Outcome> outcome;

    public GameFragment(ImmutableList<TurnEntry<T>> turns, Optional<Outcome> outcome) {
        this.turns = checkNotNull(turns);
        this.outcome = checkNotNull(outcome);
    }

    public ImmutableList<TurnEntry<T>> turns() {
        return turns;
    }

    public TurnEntry<T> turn(int i) {
        return turns.get(i);
    }

    public Optional<Outcome> outcome() {
        return outcome;
    }

    public int length() {
        return turns.size();
    }

    public <S> GameFragment<S> map(Function<? super T, ? extends S> f) {
        return new GameFragment<>(map(a -> a.map(f), turns), outcome);
    }

    public GameFragment<T> withoutActions() {
        return new GameFragment<>(map(a -> a.withoutAction(), turns), outcome);
    }

    public GameFragment<T> withoutOutcome() {
        return new GameFragment<>(turns, Optional.empty());
    }

    public GameFragment<T> classify(Function<Iterable<TurnEntry<T>>, Optional<Outcome>> classifier) {
        return new GameFragment<>(turns, classifier.apply(turns));
    }

    public GameFragment<T> putActionAt(int idx, Action action) {
        checkArgument(idx >= 0 && idx < length());

        ImmutableList.Builder<TurnEntry<T>> builder = ImmutableList.builder();
        for (int i = 0; i < turns.size(); ++i) {
            TurnEntry<T> entry = turns.get(i);
            builder.add(i == idx ? entry.withAction(action) : entry);
        }
        return new GameFragment<>(builder.build(), outcome);
    }

    public GameFragment<T> delay(int k) {
        checkArgument(k > 0 && k <= length(), "Delay greater than length");

        ImmutableList.Builder<TurnEntry<T>> builder = ImmutableList.builder();
        for (int i = 0; i < k; ++i) {
            builder.add(TurnEntry.empty());
        }
        for (int i = 0; i < length() - k; ++i) {
            builder.add(turns.get(i));
        }
        return new GameFragment<>(builder.build(), outcome);
    }

    private static <T, S> ImmutableList<T> map(Function<S, T> f, Iterable<S> items) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        items.forEach(a -> builder.add(f.apply(a)));
        return builder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameFragment<?>) {
            GameFragment<?> other = (GameFragment<?>) obj;
            return turns.equals(other.turns) && outcome == other.outcome;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(turns, outcome);
    }

}
