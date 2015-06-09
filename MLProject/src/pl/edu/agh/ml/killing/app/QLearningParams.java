package pl.edu.agh.ml.killing.app;

import java.util.Optional;

import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.qlearning.Algorithm;
import pl.edu.agh.ml.killing.qlearning.QLearningConfig;
import pl.edu.agh.ml.killing.qlearning.QLearningPlayer;
import pl.edu.agh.ml.killing.runner.Player;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class QLearningParams {

    @Parameter(names = "--algorithm", converter = QLearningAlgorithmParser.class)
    public Algorithm algorithm = Algorithm.BASIC;

    @Parameter(names = "--epsilon")
    public double initEpsilon = QLearningConfig.DEFAULT_INIT_EPSILON;

    @Parameter(names = "--learning-rate")
    public double learningRate = QLearningConfig.DEFAULT_LEARNING_RATE;

    @Parameter(names = "--discount-factor")
    public double discountFactor = QLearningConfig.DEFAULT_DISCOUNT_FACTOR;

    @Parameter(names = "--epsilon-decay")
    public double epsilonDecay = QLearningConfig.DEFAULT_EPSILON_DECAY;

    @Parameter(names = "--learning-decay")
    public double learningRateDecay = QLearningConfig.DEFAULT_LEARNING_RATE_DECAY;

    @Parameter(names = "--lambda")
    public Double lambda = null;

    public QLearningConfig config() {
        return QLearningConfig.builder()
                .withAlgorithm(algorithm)
                .withInitEpsilon(initEpsilon)
                .withLearningRate(learningRate)
                .withDiscountFactor(discountFactor)
                .withEpsilonDecay(epsilonDecay)
                .withLearningRateDecay(learningRateDecay)
                .withMaybeLambda(Optional.ofNullable(lambda))
                .build();
    }

    public Player player(StateParams state, GameConfig gameConfig) {
        return new QLearningPlayer<>(config(), gameConfig, state.stateConverter());
    }
}
