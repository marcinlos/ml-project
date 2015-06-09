package pl.edu.agh.ml.killing.app;

import pl.edu.agh.ml.killing.qlearning.Algorithm;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class QLearningAlgorithmParser implements IStringConverter<Algorithm> {

    @Override
    public Algorithm convert(String name) {
        try {
            return Algorithm.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ParameterException(name + ": no such Q-learning algorithm");
        }
    }

}
