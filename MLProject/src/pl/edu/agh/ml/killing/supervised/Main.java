package pl.edu.agh.ml.killing.supervised;

import java.util.Optional;
import java.util.function.Function;

import pl.edu.agh.ml.killing.RandomAI;
import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.report.ActionStatistics;
import pl.edu.agh.ml.killing.report.SysoReporter;
import pl.edu.agh.ml.killing.runner.Driver;
import pl.edu.agh.ml.killing.runner.Player;
import pl.edu.agh.ml.killing.runner.TestConfig;
import pl.edu.agh.ml.killing.state.StateInfo;
import pl.edu.agh.ml.killing.state.partial.PlayerVicinity;
import pl.edu.agh.ml.killing.supervised.weka.WekaClassifier;
import pl.edu.agh.ml.killing.supervised.weka.WekaClosestEnemies;
import pl.edu.agh.ml.killing.supervised.weka.WekaFormat;
import weka.classifiers.trees.J48;

import com.google.common.eventbus.EventBus;

public class Main {

    public static void main(String[] args) {
        GameConfig gameConfig = GameConfig.builder()
                .withMapSize(10, 10)
                .withPlayerHp(3)
                .withEnemies(4)
                .withEnemyHp(1)
                .withAI(RandomAI::new)
                .build();

        TestConfig config = TestConfig.builder()
                .withGameConfig(gameConfig)
                .withGameCount(10000)
                .withMaxGameRounds(1000)
                .build();

        EventBus bus = new EventBus();
        bus.register(new SysoReporter(10));
        bus.register(new ActionStatistics(10));

        int windowSize = 4;
        int closest = 1;
        Function<StateInfo, PlayerVicinity> f = info -> PlayerVicinity.closest(info, closest);
        WekaFormat<GameFragment<PlayerVicinity>> format = new WekaClosestEnemies(windowSize, closest);
        GameFragmentClassifier<PlayerVicinity> classifier = new WekaClassifier<PlayerVicinity>(format, J48::new);

        Player player = new SupervisedPlayer<PlayerVicinity>(windowSize, f, classifier);

        Driver driver = new Driver(player, config, Optional.of(bus));
        driver.run();
    }

}
