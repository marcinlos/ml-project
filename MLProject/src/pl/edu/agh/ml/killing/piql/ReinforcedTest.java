package pl.edu.agh.ml.killing.piql;

import java.text.MessageFormat;
import java.util.function.Function;

import pl.edu.agh.ml.killing.RandomAI;
import pl.edu.agh.ml.killing.game.GameConfig;
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

    private double gamma = 1.0;
    private double epsilon = 0.5;

    public ReinforcedTest(GameConfig config, Function<StateInfo, T> mapper) {
        this.environment = new PiqleEnvironment<>(config, mapper);
        this.selector = new QLearningSelector();
        this.agent = new AbstractAgent(environment, selector);
        this.referee = new OnePlayerReferee(agent);

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

    public void run(int iters, int N) {
        int won = 0;
        for (int i = 0; i < iters; ++i) {
            referee.episode(environment.defaultInitialState());
            if (referee.getWinner() == -1) {
                won += 1;
            }
            if (i > 0 && i % N == 0) {
                String msg = MessageFormat.format("After {0}: {1} / {2} ({3,number,#.##%})", i,
                        won, N, won / (double) N);
                System.out.println(msg);
                won = 0;
            }
            // Decay epsilon
            epsilon *= 0.99999;
            setSelectorParams();
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
        ReinforcedTest<PlayerVicinity> test = new ReinforcedTest<>(config,
                PlayerVicinity.withRadius(radius));
        test.run(15000, 500);
    }
}
