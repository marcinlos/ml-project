package pl.edu.agh.ml.killing.app;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class LearningParser implements IStringConverter<Learning> {

    @Override
    public Learning convert(String name) {
        try {
            return Learning.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ParameterException(name + ": no such learning type");
        }
    }

}