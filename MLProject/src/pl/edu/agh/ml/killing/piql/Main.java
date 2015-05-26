package pl.edu.agh.ml.killing.piql;

import java.util.Optional;

import pl.edu.agh.ml.killing.RandomAI;
import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.report.SysoReporter;
import pl.edu.agh.ml.killing.runner.Driver;
import pl.edu.agh.ml.killing.runner.Player;
import pl.edu.agh.ml.killing.runner.TestConfig;
import pl.edu.agh.ml.killing.state.partial.PlayerVicinity;

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
                .withGameCount(50000)
                .withMaxGameRounds(1000)
                .build();

        EventBus bus = new EventBus();
        bus.register(new SysoReporter(500));

        QLearningConfig learningConfig = QLearningConfig.builder()
                .withDiscountFactor(1)
                .withAlgorithm(Algorithm.WATKINS)
                .withLambda(0.7)
                .build();

        int radius = 2;
        Player player = new QLearningPlayer<>(learningConfig, gameConfig, PlayerVicinity.withRadius(radius));

        Driver driver = new Driver(player, config, Optional.of(bus));
        driver.run();
    }

}
