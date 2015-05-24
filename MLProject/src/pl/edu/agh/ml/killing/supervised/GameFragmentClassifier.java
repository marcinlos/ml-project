package pl.edu.agh.ml.killing.supervised;

public interface GameFragmentClassifier<T> {

    void addExample(GameFragment<T> item);

    double[] classify(GameFragment<T> item);

    void update();

}