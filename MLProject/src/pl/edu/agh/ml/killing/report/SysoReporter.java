package pl.edu.agh.ml.killing.report;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;
import java.util.Optional;

import pl.edu.agh.ml.killing.core.Result;

public class SysoReporter implements Reporter {

    private final int reportInterval;

    private int iter = 0;
    private int won = 0;

    public SysoReporter(int reportInterval) {
        checkArgument(reportInterval >= 0);
        this.reportInterval = reportInterval;
    }

    @Override
    public void nextGame(Optional<Result> result) {
        result.ifPresent(r -> {
            if (r == Result.WON)
                ++won;
        });

        if (shouldReport()) {
            String msg = MessageFormat.format("After {0}: {1} / {2} ({3,number,#.##%})", iter,
                    won, reportInterval, won / (double) reportInterval);
            System.out.println(msg);
            won = 0;
        }
        ++ iter;
    }

    private boolean shouldReport() {
        return iter > 0 && iter % reportInterval == 0;
    }
}