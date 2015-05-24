package pl.edu.agh.ml.killing.piql;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.RandomAI;
import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.report.Reporter;
import pl.edu.agh.ml.killing.report.SysoReporter;
import pl.edu.agh.ml.killing.state.StateInfo;
import pl.edu.agh.ml.killing.state.partial.PlayerVicinity;
import referees.OnePlayerReferee;
import agents.AbstractAgent;
import agents.IAgent;
import algorithms.QLearningSelector;

public class ReinforcedTest<T> {

    private final PiqleEnvironment<T> environment;
    private final IAgent agent;
    private final OnePlayerReferee referee;
    private final QLearningSelector selector;

    private final Optional<Reporter> reporter;

    private double gamma = 1.0;
    private double epsilon = 0.5;

    private ReinforcedTest(GameConfig config, Function<StateInfo, T> mapper,
            Optional<Reporter> reporter) {
        this.environment = new PiqleEnvironment<>(config, mapper);
        this.selector = new QLearningSelector();
        this.agent = new AbstractAgent(environment, selector);
        this.referee = new OnePlayerReferee(agent);
        this.reporter = checkNotNull(reporter);

        configure();
    }

    public ReinforcedTest(GameConfig config, Function<StateInfo, T> mapper) {
        this(config, mapper, Optional.empty());
    }

    public ReinforcedTest(GameConfig config, Function<StateInfo, T> mapper, Reporter reporter) {
        this(config, mapper, Optional.of(reporter));
    }

    private void configure() {
        setSelectorParams();
        referee.setMaxIter(1000);
    }

    private void setSelectorParams() {
        selector.setGamma(gamma);
        selector.setEpsilon(epsilon);
    }

    public void run(int iters) {
        for (int i = 0; i < iters; ++i) {
            referee.episode(environment.defaultInitialState());
            reporter.ifPresent(r -> r.nextGame(resultFrom(referee.getWinner())));

            // Decay epsilon
            epsilon *= 0.99999;
            setSelectorParams();
        }
    }

    private Optional<Result> resultFrom(int r) {
        switch (r) {
        case -1:
            return Optional.of(Result.WON);
        case 1:
            return Optional.of(Result.LOST);
        default:
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        GameConfig config = GameConfig.builder()
                .withMapSize(10, 10)
                .withPlayerHp(3)
                .withEnemies(4)
                .withEnemyHp(1)
                .withAI(RandomAI::new)
                .build();

        int radius = 2;
        int iters = 15000;

        ReinforcedTest<PlayerVicinity> test = new ReinforcedTest<>(config,
                PlayerVicinity.withRadius(radius), new SysoReporter(500));
        test.run(iters);
    }
}
