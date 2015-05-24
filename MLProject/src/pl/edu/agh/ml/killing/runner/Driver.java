package pl.edu.agh.ml.killing.runner;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.game.GameEngine;
import pl.edu.agh.ml.killing.state.StateInfo;

import com.google.common.eventbus.EventBus;

public class Driver {

    private final Player player;
    private final TestConfig config;
    private final Optional<EventBus> eventBus;

    public Driver(Player player, TestConfig config, Optional<EventBus> eventBus) {
        this.player = checkNotNull(player);
        this.config = checkNotNull(config);
        this.eventBus = checkNotNull(eventBus);
    }

    public void run() {
        for (int i = 0; i < config.gameCount(); ++i) {
            playOneGame();
        }
    }

    private void playOneGame() {
        GameEngine game = GameEngine.create(config.gameConfig());

        int round = 0;
        while (!game.finished() && round < config.maxGameRounds()) {
            StateInfo state = game.snapshot();
            Action action = player.chooseAction(state, game.availableActions());
            game.playRound(action);
        }

        player.gameFinished(game.result());
        eventBus.ifPresent(bus -> bus.post(GameFinishedEvent.from(game)));
    }

}
