package pl.edu.agh.ml.killing.game;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.isNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import pl.edu.agh.ml.killing.core.Entity;
import pl.edu.agh.ml.killing.core.MapExtent;
import pl.edu.agh.ml.killing.core.Position;
import pl.edu.agh.ml.killing.core.Side;

class BattleBuilder {

    private final Random random = new Random();
    private final GameConfig config;

    public BattleBuilder(GameConfig config) {
        assert !isNull(config);
        this.config = config;
    }

    public Battlefield newBattlefield() {
        return new Battlefield(config.mapExtent());
    }

    public Entity addPlayer(Battlefield field) {
        Entity player = new Entity(Side.PLAYER, config.playerHp(), playerPosition(config));
        field.add(player);
        return player;
    }

    public Set<Enemy> createEnemies(Battlefield field) {
        int hp = config.enemyHp();
        Set<Enemy> enemies = new HashSet<>();
        for (int i = 0; i < config.enemiesCount(); ++i) {
            Position pos = findFreeSpot(field);
            Entity entity = new Entity(Side.ENEMY, hp, pos);
            field.add(entity);
            Enemy enemy = new Enemy(entity, config.newAI());
            enemies.add(enemy);
        }
        return enemies;
    }

    private Position findFreeSpot(Battlefield field) {
        checkState(field.freeFields() > 0);
        while (true) {
            Position pos = randomPosition();
            if (field.isEmpty(pos)) {
                return pos;
            }
        }
    }

    private Position playerPosition(GameConfig config) {
        return config.playerPosition().orElse(randomPosition());
    }

    private Position randomPosition() {
        MapExtent range = config.mapExtent();
        int x = random.nextInt(range.width());
        int y = random.nextInt(range.height());
        return new Position(x, y);
    }
}