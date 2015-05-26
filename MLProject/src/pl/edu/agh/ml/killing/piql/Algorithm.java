package pl.edu.agh.ml.killing.piql;

import java.util.function.Function;

import algorithms.PengSelector;
import algorithms.QLearningSelector;
import algorithms.WatkinsSelector;

public enum Algorithm {
    BASIC(config -> new QLearningSelector()),
    PENG(withLambda(PengSelector::new)),
    WATKINS(withLambda(WatkinsSelector::new));

    private final Function<QLearningConfig, QLearningSelector> selectorFactory;

    Algorithm(Function<QLearningConfig, QLearningSelector> factory) {
        this.selectorFactory = factory;
    }

    public QLearningSelector newSelector(QLearningConfig config) {
        return selectorFactory.apply(config);
    }

    private static <T> Function<QLearningConfig, T> withLambda(Function<Double, T> f) {
        return config -> {
            if (config.lambda().isPresent()) {
                return f.apply(config.lambda().get());
            } else {
                throw new IllegalArgumentException("Lambda not specified");
            }
        };
    }
}
