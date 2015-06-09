package pl.edu.agh.ml.killing.app;

import java.util.function.Function;

import pl.edu.agh.ml.killing.state.StateInfo;
import pl.edu.agh.ml.killing.state.partial.PlayerVicinity;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.ImmutableMap;

public class StateParser implements IStringConverter<Function<Integer, Function<StateInfo, ?>>> {

    private static final ImmutableMap<String, Function<Integer, Function<StateInfo, ?>>> STATE_TYPES =
            ImmutableMap.<String, Function<Integer, Function<StateInfo, ?>>> builder()
                    .put("closest", PlayerVicinity::closest)
                    .put("nearby", PlayerVicinity::withRadius)
                    .build();

    @Override
    public Function<Integer, Function<StateInfo, ?>> convert(String name) {
        if (!STATE_TYPES.containsKey(name)) {
            throw new ParameterException(STATE_TYPES + ": no such state");
        }
        return STATE_TYPES.get(name);
    }

}
