package pl.edu.agh.ml.killing.app;

import pl.edu.agh.ml.killing.runner.Player;
import pl.edu.agh.ml.killing.supervised.GameFragmentClassifier;
import pl.edu.agh.ml.killing.supervised.SupervisedPlayer;
import pl.edu.agh.ml.killing.supervised.weka.WekaClassifier;
import pl.edu.agh.ml.killing.supervised.weka.WekaClosestEnemies;
import pl.edu.agh.ml.killing.supervised.weka.WekaFormat;
import weka.classifiers.trees.J48;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class SupervisedLearningParams {

    @Parameter(names = "--window")
    public Integer windowSize = 10;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Player player(StateParams state) {
        WekaFormat<?> format = new WekaClosestEnemies(windowSize, state.stateSize);
        GameFragmentClassifier<?> classifier = new WekaClassifier(format, J48::new);
        return new SupervisedPlayer(windowSize, state.stateConverter(), classifier);
    }

}
