package pl.edu.agh.ml.killing.piql;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.RandomAI;
import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.report.SysoReporter;
import pl.edu.agh.ml.killing.runner.events.GameFinishedEvent;
import pl.edu.agh.ml.killing.state.StateInfo;
import pl.edu.agh.ml.killing.state.partial.PlayerVicinity;
import referees.OnePlayerReferee;
import agents.AbstractAgent;
import agents.IAgent;
import algorithms.QLearningSelector;

import com.google.common.eventbus.EventBus;

public class ReinforcedTest<T> {

    private final PiqleEnvironment<T> environment;
    private final IAgent agent;
    private final OnePlayerReferee referee;
    private final QLearningSelector selector;

    private final Optional<EventBus> eventBus;

    private double gamma = 1.0;
    private double epsilon = 0.5;

    public ReinforcedTest(GameConfig config, Function<StateInfo, T> mapper,
            Optional<EventBus> eventBus) {
        this.environment = new PiqleEnvironment<>(config, mapper);
        this.selector = new QLearningSelector();
        this.agent = new AbstractAgent(environment, selector);
        this.referee = new OnePlayerReferee(agent);
        this.eventBus = checkNotNull(eventBus);

        configure();
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

            Optional<Result> result = resultFrom(referee.getWinner());
            eventBus.ifPresent(bus -> bus.post(new GameFinishedEvent(result)));

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

        EventBus bus = new EventBus();
        SysoReporter reporter = new SysoReporter(500);
        bus.register(reporter);

        ReinforcedTest<PlayerVicinity> test = new ReinforcedTest<>(config,
                PlayerVicinity.withRadius(radius), Optional.of(bus));
        test.run(iters);
    }
}
