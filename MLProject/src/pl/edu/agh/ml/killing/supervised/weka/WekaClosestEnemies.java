package pl.edu.agh.ml.killing.supervised.weka;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Direction;
import pl.edu.agh.ml.killing.state.EntityInfo;
import pl.edu.agh.ml.killing.state.partial.PlayerVicinity;
import pl.edu.agh.ml.killing.supervised.GameFragment;
import pl.edu.agh.ml.killing.supervised.Outcome;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class WekaClosestEnemies implements WekaFormat<GameFragment<PlayerVicinity>> {

    private static final ImmutableList<String> OUTCOMES;

    static {
        ImmutableList.Builder<String> outcomes = ImmutableList.builder();
        for (Outcome outcome : Outcome.values()) {
            outcomes.add(outcome.toString());
        }
        OUTCOMES = outcomes.build();
    }

    private static final ImmutableList<String> ACTIONS;

    static {
        ImmutableList.Builder<String> actions = ImmutableList.builder();
        for (Direction d : Direction.values()) {
            actions.add(new Action.Attack(d).toString());
            actions.add(new Action.Move(d).toString());
        }
        actions.add(new Action.Idle().toString());
        ACTIONS = actions.build();
    }

    interface Extractor {
        Optional<Double> extract(Attribute attribute, GameFragment<PlayerVicinity> data);
    }

    private final ImmutableList<Attribute> attributes;
    private final Attribute classAttribute;
    private final ImmutableMap<Attribute, Extractor> extractors;

    public WekaClosestEnemies(int windowSize, int count) {
        checkArgument(windowSize > 0);
        checkArgument(count > 0);

        this.classAttribute = new Attribute("outcome", OUTCOMES);
        this.extractors = makeExtractors(classAttribute, windowSize, count);
        this.attributes = ImmutableList.copyOf(extractors.keySet());
    }

    private static ImmutableMap<Attribute, Extractor> makeExtractors(Attribute outcome,
            int windowSize, int enemyCount) {
        ImmutableMap.Builder<Attribute, Extractor> attributes = ImmutableMap.builder();

        for (int delay = 0; delay < windowSize; ++delay) {
            attributes.put(new Attribute("hp-after" + delay), getPlayerHp(delay));
            for (int i = 0; i < enemyCount; ++i) {
                attributes.putAll(extractorsForEnemy(delay, i));
            }
            attributes.put(new Attribute("action-" + delay, ACTIONS), getAction(delay));
        }
        attributes.put(outcome, getOutcome());
        return attributes.build();
    }

    private static ImmutableMap<Attribute, Extractor> extractorsForEnemy(int delay, int i) {
        String postfix = "-" + i + "-after-" + delay;
        return ImmutableMap.<Attribute, Extractor> builder()
                .put(new Attribute("hp" + postfix), getEnemyHp(delay, i))
                .put(new Attribute("dx" + postfix), getEnemyDx(delay, i))
                .put(new Attribute("dy" + postfix), getEnemyDy(delay, i))
                .build();
    }

    private static Extractor getPlayerHp(int delay) {
        return (a, data) -> data.turn(delay).state().map(state -> (double) state.player().hp());
    }

    private static Extractor getAction(int delay) {
        return (a, data) -> data.turn(delay).action().map(action -> {
            return (double) a.indexOfValue(action.toString());
        });
    }

    private static Extractor getOutcome() {
        return (a, data) -> data.outcome().map(out -> (double) a.indexOfValue(out.toString()));
    }

    private static Extractor fromEnemy(int delay, int i, Function<EntityInfo, Double> f) {
        return (a, data) -> {
            return data.turn(delay).state().flatMap(state -> {
                return Optional.ofNullable(Iterables.get(state.enemies(), i, null)).map(f);
            });
        };
    }

    private static Extractor getEnemyHp(int delay, int i) {
        return fromEnemy(delay, i, e -> (double) e.hp());
    }

    private static Extractor getEnemyDx(int delay, int i) {
        return fromEnemy(delay, i, e -> (double) e.position().x);
    }

    private static Extractor getEnemyDy(int delay, int i) {
        return fromEnemy(delay, i, e -> (double) e.position().y);
    }

    @Override
    public Instance convert(GameFragment<PlayerVicinity> item) {
        double[] vals = new double[attributes.size()];
        for (int i = 0; i < vals.length; ++i) {
            Attribute attribute = attributes.get(i);
            Extractor extractor = extractors.get(attribute);
            double value = extractor.extract(attribute, item).orElse(Utils.missingValue());
            vals[i] = value;
        }
        return new DenseInstance(1.0, vals);
    }

    @Override
    public Instances newBuffer() {
        Instances instances = new Instances("states", new ArrayList<>(attributes), 1000);
        instances.setClass(classAttribute);
        return instances;
    }

}
