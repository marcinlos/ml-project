package pl.edu.agh.ml.killing.app;

import java.util.function.Function;

import pl.edu.agh.ml.killing.state.StateInfo;
import pl.edu.agh.ml.killing.state.partial.PlayerVicinity;

import com.beust.jcommander.Parameter;

public class StateParams {

    @Parameter(names = "--state-size", description = "State parameter, meaning depends on state type")
    public Integer stateSize = 2;

    @Parameter(names = "--state", converter = StateParser.class)
    public Function<Integer, Function<StateInfo, ?>> stateConverter = PlayerVicinity::closest;

    public Function<StateInfo, ?> stateConverter() {
        return stateConverter.apply(stateSize);
    }
}
