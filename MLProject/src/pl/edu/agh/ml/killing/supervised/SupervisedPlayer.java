package pl.edu.agh.ml.killing.supervised;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.runner.Player;
import pl.edu.agh.ml.killing.state.StateInfo;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class SupervisedPlayer<T> implements Player {

    private final int windowSize;
    private final SlidingWindow<TurnEntry<StateInfo>> window;
    private final Function<StateInfo, T> stateConverter;
    private final GameFragmentClassifier<T> classifier;

    private int exampleCount = 0;

    public SupervisedPlayer(int windowSize, Function<StateInfo, T> stateConverter,
            GameFragmentClassifier<T> classifier) {
        Preconditions.checkArgument(windowSize > 0);

        this.windowSize = windowSize;
        this.window = new SlidingWindow<>(windowSize);
        this.stateConverter = checkNotNull(stateConverter);
        this.classifier = checkNotNull(classifier);
    }

    @Override
    public Action chooseAction(StateInfo state, ImmutableSet<Action> availableActions) {
        Action action = choose(availableActions);
        process(state, action);
        return action;
    }

    private Action choose(Collection<Action> actions) {
        if (exampleCount < 5000) {
            return random(actions);
        } else {
            return chooseWithClassifier(actions);
        }
    }

    private Action chooseWithClassifier(Collection<Action> actions) {
        GameFragment<T> fragment = currentFragment().map(stateConverter).withoutActions();

        Action bestAction = null;
        double maxDispersion = Double.NEGATIVE_INFINITY;

        for (int i = 1; i < windowSize; ++i) {
            GameFragment<T> delayed = fragment.delay(i);
            double minQ = Double.POSITIVE_INFINITY;
            double maxQ = Double.NEGATIVE_INFINITY;
            Action maxAction = null;
            for (Action action : actions) {
                double q = predictQuality(delayed.putActionAt(i, action));
                if (q < minQ) {
                    minQ = q;
                }
                if (q > maxQ) {
                    maxQ = q;
                    maxAction = action;
                }
            }
            double dispersion = maxQ - minQ;
            if (dispersion > maxDispersion) {
                bestAction = maxAction;
                maxDispersion = dispersion;
            }
        }
        return bestAction;
    }

    private double predictQuality(GameFragment<T> fragment) {
        // TODO: is it good enough?
        double[] distribution = classifier.classify(fragment);
        return distribution[Outcome.POSITIVE.ordinal()];
    }

    private Action random(Collection<Action> actions) {
        List<Action> actionList = new ArrayList<>(actions);
        Collections.shuffle(actionList);
        return actionList.get(0);
    }

    @Override
    public void gameFinished(StateInfo state, Optional<Result> result) {
        classifier.update();
    }

    private void process(StateInfo state, Action action) {
        window.accept(TurnEntry.of(state, action));

        classifier.addExample(makeExample());
        ++exampleCount;
    }

    private GameFragment<StateInfo> currentFragment() {
        return new GameFragment<>(padToWindowSize(window.current()), Optional.empty());
    }

    private GameFragment<T> makeExample() {
        return currentFragment()
                .classify(this::classifyFragment)
                .map(stateConverter);
    }

    private Optional<Outcome> classifyFragment(Iterable<TurnEntry<StateInfo>> turns) {
        ImmutableList<StateInfo> states = presentStates(turns);
        int count = states.size();
        if (count > 1) {
            StateInfo before = states.get(0);
            StateInfo after = states.get(count - 1);
            return Optional.of(compare(before, after));
        } else {
            return Optional.empty();
        }
    }

    private static <T> ImmutableList<T> presentStates(Iterable<TurnEntry<T>> turns) {
        ImmutableList.Builder<T> states = ImmutableList.builder();
        turns.forEach(turn -> turn.state().ifPresent(states::add));
        return states.build();
    }

    private Outcome compare(StateInfo before, StateInfo after) {
        int d = eval(after) - eval(before);
        return d < 0 ? Outcome.NEGATIVE : d > 0 ? Outcome.POSITIVE : Outcome.NEUTRAL;
    }

    private int eval(StateInfo info) {
        if (info.player().isDead()) {
            return -100000;
        } else {
            return info.player().hp() - 2 * info.enemies().size();
        }
    }

    private <S> ImmutableList<TurnEntry<S>> padToWindowSize(ImmutableList<TurnEntry<S>> states) {
        int missing = windowSize - states.size();
        if (missing == 0) {
            return states;
        } else {
            return ImmutableList.<TurnEntry<S>> builder()
                    .addAll(states)
                    .addAll(Collections.nCopies(missing, TurnEntry.empty()))
                    .build();
        }
    }

}
