package pl.edu.agh.ml.killing.supervised;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.LinkedList;

import com.google.common.collect.ImmutableList;

class SlidingWindow<T> {

    private final LinkedList<T> window = new LinkedList<>();
    private int windowSize;

    public SlidingWindow(int size) {
        checkArgument(size > 0, "Non-positive sliding window width");
        this.windowSize = size;
    }

    public void accept(T value) {
        window.push(value);
        if (window.size() > windowSize) {
            window.removeLast();
        }
    }

    public ImmutableList<T> current() {
        return ImmutableList.copyOf(window);
    }

}
