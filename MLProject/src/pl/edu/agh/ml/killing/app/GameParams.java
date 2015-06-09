package pl.edu.agh.ml.killing.app;

import java.util.function.Supplier;

import pl.edu.agh.ml.killing.RandomAI;
import pl.edu.agh.ml.killing.game.EnemyAI;
import pl.edu.agh.ml.killing.game.GameConfig;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class GameParams {

    @Parameter(names = "--map-width")
    public Integer mapWidth = 10;

    @Parameter(names = "--map-height")
    public Integer mapHeight = 10;

    @Parameter(names = "--player-hp")
    public Integer playerHp = 3;

    @Parameter(names = "--enemies")
    public Integer enemies = 4;

    @Parameter(names = "--enemy-hp")
    public Integer enemyHp = 1;

    @Parameter(names = "--ai", converter = EnemyAIParser.class)
    public Supplier<? extends EnemyAI> ai = RandomAI::new;

    public GameConfig gameConfig() {
        return GameConfig.builder()
                .withMapSize(mapWidth, mapHeight)
                .withPlayerHp(playerHp)
                .withEnemies(enemies)
                .withEnemyHp(enemyHp)
                .withAI(ai)
                .build();
    }
}
