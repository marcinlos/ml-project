package pl.edu.agh.ml.killing.runner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import pl.edu.agh.ml.killing.game.GameConfig;

public final class TestConfig {

    private final int maxGameRounds;
    private final int gameCount;
    private final GameConfig gameConfig;

    private TestConfig(int maxGameRounds, int gameCount, GameConfig gameConfig) {
        this.maxGameRounds = maxGameRounds;
        this.gameCount = gameCount;
        this.gameConfig = gameConfig;
    }

    public int maxGameRounds() {
        return maxGameRounds;
    }

    public int gameCount() {
        return gameCount;
    }

    public GameConfig gameConfig() {
        return gameConfig;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer maxGameRounds;
        private Integer gameCount;
        private GameConfig gameConfig;

        public Builder withMaxGameRounds(int maxGameRounds) {
            checkArgument(maxGameRounds > 0);
            this.maxGameRounds = maxGameRounds;
            return this;
        }

        public Builder withGameCount(int gameCount) {
            checkArgument(gameCount > 0);
            this.gameCount = gameCount;
            return this;
        }

        public Builder withGameConfig(GameConfig gameConfig) {
            this.gameConfig = checkNotNull(gameConfig);
            return this;
        }

        public TestConfig build() {
            checkNotNull(maxGameRounds, "Max number of rounds not set");
            checkNotNull(gameCount, "Number of games not set");

            return new TestConfig(maxGameRounds, gameCount, gameConfig);
        }
    }

}
