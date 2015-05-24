package pl.edu.agh.ml.killing.supervised.weka;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Supplier;

import pl.edu.agh.ml.killing.supervised.GameFragment;
import pl.edu.agh.ml.killing.supervised.GameFragmentClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class WekaClassifier<T> implements GameFragmentClassifier<T> {

    private final WekaFormat<GameFragment<T>> format;
    private final Supplier<Classifier> factory;
    private final Instances exampleBuffer;

    private Classifier classifier;

    public WekaClassifier(WekaFormat<GameFragment<T>> format, Supplier<Classifier> factory) {
        this.format = checkNotNull(format);
        this.factory = checkNotNull(factory);
        this.exampleBuffer = format.newBuffer();
        update();
    }

    private Instance convert(GameFragment<T> item) {
        Instance instance = format.convert(item);
        instance.setDataset(exampleBuffer);
        return instance;
    }

    @Override
    public void addExample(GameFragment<T> item) {
        exampleBuffer.add(convert(item));
    }

    @Override
    public double[] classify(GameFragment<T> item) {
        try {
            return classifier.distributionForInstance(convert(item));
        } catch (Exception e) {
            throw new RuntimeException("Weka problem", e);
        }
    }

    @Override
    public void update() {
        classifier = factory.get();
        try {
            classifier.buildClassifier(exampleBuffer);
        } catch (Exception e) {
            throw new RuntimeException("Weka problem", e);
        }
    }

}