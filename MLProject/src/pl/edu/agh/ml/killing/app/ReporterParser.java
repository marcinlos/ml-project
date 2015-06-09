package pl.edu.agh.ml.killing.app;

import java.util.function.Function;

import pl.edu.agh.ml.killing.report.ActionStatistics;
import pl.edu.agh.ml.killing.report.SysoReporter;
import pl.edu.agh.ml.killing.report.TestReporter;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.ImmutableMap;

public class ReporterParser implements IStringConverter<Function<Integer, ?>> {

    private static final ImmutableMap<String, Function<Integer, ?>> REPORTS =
            ImmutableMap.<String, Function<Integer, ?>> builder()
                    .put("ActionStatistics", ActionStatistics::new)
                    .put("SysoReporter", SysoReporter::new)
                    .put("TestReporter", TestReporter::new)
                    .build();

    @Override
    public Function<Integer, ?> convert(String name) {
        if (!REPORTS.containsKey(name)) {
            throw new ParameterException(name + ": no such reporter");
        }
        return REPORTS.get(name);
    }

}
