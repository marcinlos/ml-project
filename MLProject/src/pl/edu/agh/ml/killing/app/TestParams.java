package pl.edu.agh.ml.killing.app;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import pl.edu.agh.ml.killing.game.GameConfig;
import pl.edu.agh.ml.killing.runner.TestConfig;

import com.beust.jcommander.Parameter;

public class TestParams {

    @Parameter(names = "--game-count")
    public Integer gameCount = 10000;

    @Parameter(names = "--max-rounds")
    public Integer maxGameRounds = 5000;

    @Parameter(names = "--report-interval")
    public Integer reportInterval = 500;

    @Parameter(names = "--report", converter = ReporterParser.class)
    public List<Function<Integer, ?>> reports = new ArrayList<>();

    @Parameter(names = "--learning", converter = LearningParser.class, required = true)
    public Learning learning;

    public TestConfig testConfig(GameConfig gameConfig) {
        return TestConfig.builder()
                .withGameConfig(gameConfig)
                .withGameCount(gameCount)
                .withMaxGameRounds(maxGameRounds)
                .build();
    }

    public List<?> reporters() {
        return reports.stream()
                .map(f -> f.apply(reportInterval))
                .collect(Collectors.toList());
    }
}
