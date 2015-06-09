package pl.edu.agh.ml.killing.app;

import java.util.function.Supplier;

import pl.edu.agh.ml.killing.RandomAI;
import pl.edu.agh.ml.killing.game.EnemyAI;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.ImmutableMap;

class EnemyAIParser implements IStringConverter<Supplier<EnemyAI>> {

    private static final ImmutableMap<String, Supplier<EnemyAI>> AVAILABLE_AI =
            ImmutableMap.<String, Supplier<EnemyAI>> builder()
                    .put("random", RandomAI::new)
                    .build();

    @Override
    public Supplier<EnemyAI> convert(String value) {
        if (!AVAILABLE_AI.containsKey(value)) {
            throw new ParameterException(value + ": no such AI type");
        }
        return AVAILABLE_AI.get(value);
    }
}