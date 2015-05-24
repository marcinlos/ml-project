package pl.edu.agh.ml.killing.report;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;

import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.runner.GameFinishedEvent;

import com.google.common.eventbus.Subscribe;

public class SysoReporter {

    private final int reportInterval;

    private int iter = 0;
    private int won = 0;

    public SysoReporter(int reportInterval) {
        checkArgument(reportInterval >= 0);
        this.reportInterval = reportInterval;
    }

    @Subscribe
    public void gameFinished(GameFinishedEvent e) {
        ++iter;
        e.result().ifPresent(r -> {
            if (r == Result.WON) {
                ++won;
            }
        });

        if (shouldReport()) {
            String msg = MessageFormat.format("After {0}: {1} / {2} ({3,number,#.##%})", iter,
                    won, reportInterval, won / (double) reportInterval);
            System.out.println(msg);
            won = 0;
        }
    }

    private boolean shouldReport() {
        return iter % reportInterval == 0;
    }
}