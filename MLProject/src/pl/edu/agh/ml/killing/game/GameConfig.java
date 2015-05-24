package pl.edu.agh.ml.killing.game;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Supplier;

import pl.edu.agh.ml.killing.core.MapExtent;
import pl.edu.agh.ml.killing.core.Position;

public final class GameConfig {

    private final int playerHp;
    private final int enemyHp;
    private final Optional<Position> playerPosition;
    private final MapExtent mapExtent;
    private final int enemiesCount;
    private final Supplier<? extends EnemyAI> aiFactory;

    private GameConfig(int playerHp, int enemyHp, Optional<Position> playerPosition,
            MapExtent mapExtent, int enemiesCount, Supplier<? extends EnemyAI> aiFactory) {
        checkArgument(mapExtent.area() > enemiesCount, "Too many enemies, won't fit");
        playerPosition.ifPresent(pos -> {
            checkArgument(mapExtent.inRange(pos), "Player outside the range");
        });
        this.playerHp = playerHp;
        this.enemyHp = enemyHp;
        this.playerPosition = playerPosition;
        this.mapExtent = mapExtent;
        this.enemiesCount = enemiesCount;
        this.aiFactory = aiFactory;
    }

    public int playerHp() {
        return playerHp;
    }

    public int enemyHp() {
        return enemyHp;
    }

    public Optional<Position> playerPosition() {
        return playerPosition;
    }

    public MapExtent mapExtent() {
        return mapExtent;
    }

    public int enemiesCount() {
        return enemiesCount;
    }

    public EnemyAI newAI() {
        return aiFactory.get();
    }

    public static class Builder {

        private Integer playerHp;
        private Integer enemyHp;
        private Optional<Position> playerPosition = Optional.empty();
        private MapExtent mapExtent;
        private Integer enemiesCount;
        private Supplier<? extends EnemyAI> aiFactory;

        public Builder withPlayerHp(int playerHp) {
            checkArgument(playerHp > 0, "Non-positive initial player HP");
            this.playerHp = playerHp;
            return this;
        }

        public Builder withEnemyHp(int enemyHp) {
            checkArgument(enemyHp > 0, "Non-positive initial enemy HP");
            this.enemyHp = enemyHp;
            return this;
        }

        public Builder withPlayerStartingAt(Position pos) {
            this.playerPosition = Optional.of(checkNotNull(pos));
            return this;
        }

        public Builder withMapSize(int width, int height) {
            this.mapExtent = new MapExtent(width, height);
            return this;
        }

        public Builder withEnemies(int count) {
            checkArgument(count >= 0, "Negative number of enemeies");
            this.enemiesCount = count;
            return this;
        }

        public Builder withAI(Supplier<? extends EnemyAI> factory) {
            this.aiFactory = checkNotNull(factory);
            return this;
        }

        public GameConfig build() {
            checkNotNull(playerHp, "Player HP not set");
            checkNotNull(mapExtent, "Map extent not set");
            checkNotNull(aiFactory, "AI not set");

            return new GameConfig(playerHp, enemyHp, playerPosition, mapExtent, enemiesCount,
                    aiFactory);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
