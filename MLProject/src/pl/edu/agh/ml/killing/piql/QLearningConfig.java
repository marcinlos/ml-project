package pl.edu.agh.ml.killing.piql;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import algorithms.QLearningSelector;

public class QLearningConfig {

    public static final double DEFAULT_INIT_EPSILON = 0.5;
    public static final double DEFAULT_LEARNING_RATE = 0.9;
    public static final double DEFAULT_DISCOUNT_FACTOR = 0.9;
    public static final double DEFAULT_EPSILON_DECAY = 0.99999;
    public static final double DEFAULT_LEARNING_RATE_DECAY = 0.999999999;

    private final Algorithm algorithm;
    private final double initEpsilon;
    private final double learningRate;
    private final double discountFactor;
    private final double epsilonDecay;
    private final double learningRateDecay;
    private final Optional<Double> lambda;

    private QLearningConfig(Algorithm algorithm, double initEpsilon, double learningRate,
            double discountFactor, double epsilonDecay, double learningRateDecay,
            Optional<Double> lambda) {
        this.algorithm = algorithm;
        this.initEpsilon = initEpsilon;
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.epsilonDecay = epsilonDecay;
        this.learningRateDecay = learningRateDecay;
        this.lambda = lambda;
    }

    public Algorithm algorithm() {
        return algorithm;
    }

    public double initEpsilon() {
        return initEpsilon;
    }

    public double learningRate() {
        return learningRate;
    }

    public double discountFactor() {
        return discountFactor;
    }

    public double epsilonDecay() {
        return epsilonDecay;
    }

    public double learningRateDecay() {
        return learningRateDecay;
    }

    public Optional<Double> lambda() {
        return lambda;
    }

    public QLearningSelector newSelector() {
        QLearningSelector selector = algorithm.newSelector(this);
        selector.setAlpha(learningRate);
        selector.setEpsilon(initEpsilon);
        selector.setGamma(discountFactor);
        selector.setDecay(learningRateDecay);
        return selector;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Algorithm algorithm = Algorithm.BASIC;
        private double initEpsilon = DEFAULT_INIT_EPSILON;
        private double learningRate = DEFAULT_LEARNING_RATE;
        private double discountFactor = DEFAULT_DISCOUNT_FACTOR;
        private double epsilonDecay = DEFAULT_EPSILON_DECAY;
        private double learningRateDecay = DEFAULT_LEARNING_RATE_DECAY;
        private Optional<Double> lambda = Optional.empty();

        public Builder withAlgorithm(Algorithm algorithm) {
            this.algorithm = checkNotNull(algorithm);
            return this;
        }

        public Builder withInitEpsilon(double epsilon) {
            checkArgument(epsilon >= 0 && epsilon <= 1);
            this.initEpsilon = epsilon;
            return this;
        }

        public Builder withLearningRate(double learningRate) {
            checkArgument(learningRate >= 0 && learningRate <= 1);
            this.learningRate = learningRate;
            return this;
        }

        public Builder withDiscountFactor(double discountFactor) {
            checkArgument(discountFactor >= 0);
            this.discountFactor = discountFactor;
            return this;
        }

        public Builder withEpsilonDecay(double decay) {
            checkArgument(decay >= 0 && decay <= 1);
            this.epsilonDecay = decay;
            return this;
        }

        public Builder withLearningRateDecay(double decay) {
            checkArgument(decay >= 0 && decay <= 1);
            this.learningRateDecay = decay;
            return this;
        }

        public Builder withLambda(double lambda) {
            checkArgument(lambda >= 0 && lambda <= 1);
            this.lambda = Optional.of(lambda);
            return this;
        }

        public QLearningConfig build() {
            checkState(algorithm == Algorithm.BASIC || lambda.isPresent(), "Lambda not specified");
            return new QLearningConfig(algorithm, initEpsilon, learningRate, discountFactor,
                    epsilonDecay, learningRateDecay, lambda);
        }

    }

}
