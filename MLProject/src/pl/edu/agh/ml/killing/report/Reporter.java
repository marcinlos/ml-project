package pl.edu.agh.ml.killing.report;

import java.util.Optional;

import pl.edu.agh.ml.killing.core.Result;

public interface Reporter {

    void nextGame(Optional<Result> result);

}