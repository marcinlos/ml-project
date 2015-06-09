package pl.edu.agh.ml.killing.report;

import static com.google.common.base.Preconditions.checkArgument;
import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.runner.events.GameFinishedEvent;
import pl.edu.agh.ml.killing.runner.events.RoundEvent;

import com.google.common.eventbus.Subscribe;

public class TestReporter {

    private final int reportInterval;
    private final long startTime = System.currentTimeMillis();

    private int rounds = 0;
    private int games = 0;
    private int won = 0;

    public TestReporter(int reportInterval) {
        checkArgument(reportInterval >= 0);
        this.reportInterval = reportInterval;
    }

    @Subscribe
    public void afterRound(RoundEvent e) {
        ++rounds;
    }

    @Subscribe
    public void gameFinished(GameFinishedEvent e) {
        ++games;
        e.result().ifPresent(r -> {
            if (r == Result.WON) {
                ++won;
            }
        });

        if (shouldReport()) {
            double dt = 1e-3 * (System.currentTimeMillis() - startTime);
            String fmt = "{'games': %d, 'rounds': %d, 'time': %f, 'score': %f}";
            String msg = String.format(fmt, games, rounds, dt, won / (double) reportInterval);
            System.out.println(msg);
            won = 0;
        }
    }

    private boolean shouldReport() {
        return games % reportInterval == 0;
    }

}
