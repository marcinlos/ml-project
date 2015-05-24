package pl.edu.agh.ml.killing.supervised.weka;

import weka.core.Instance;
import weka.core.Instances;

public interface WekaFormat<T> {

    Instance convert(T item);

    Instances newBuffer();

}