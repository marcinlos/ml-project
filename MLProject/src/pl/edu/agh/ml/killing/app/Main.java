package pl.edu.agh.ml.killing.app;

import java.util.Optional;

import com.beust.jcommander.JCommander;
import com.google.common.eventbus.EventBus;

import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.runner.Driver;
import pl.edu.agh.ml.killing.runner.Player;
import pl.edu.agh.ml.killing.runner.TestConfig;

public class Main {

    public static void main(String[] args) {

        Params params = new Params();
        new JCommander(params.all()).parse(args);

        GameConfig gameConfig = params.game.gameConfig();
        TestConfig config = params.test.testConfig(gameConfig);

        EventBus bus = new EventBus();
        params.test.reporters().forEach(bus::register);

        Player player = null;
        if (params.test.learning == Learning.SUPERVISED) {
            player = params.supervised.player(params.state);
        } else {
            player = params.reinforced.player(params.state, gameConfig);
        }

        Driver driver = new Driver(player, config, Optional.of(bus));
        driver.run();
    }
}
