package pl.edu.agh.ml.killing.report;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.runner.events.GameFinishedEvent;
import pl.edu.agh.ml.killing.runner.events.RoundEvent;

import com.google.common.eventbus.Subscribe;

public class ActionStatistics {

    private final int reportInterval;
    private int iter = 0;

    private final Map<Action, Integer> counts = new HashMap<>();

    public ActionStatistics(int reportInterval) {
        checkArgument(reportInterval >= 0);
        this.reportInterval = reportInterval;
    }

    @Subscribe
    public void roundFinished(RoundEvent e) {
        Action a = e.action();
        counts.put(a, counts.getOrDefault(a, 0) + 1);
    }

    @Subscribe
    public void gameFinished(GameFinishedEvent e) {
        ++iter;

        if (shouldReport()) {
            printStats();
        }
    }

    private void printStats() {
        for (Entry<Action, Integer> entry : counts.entrySet()) {
            Action a = entry.getKey();
            int count = entry.getValue();
            System.out.printf("  %20s -> %d\n", a, count);
        }
        System.out.println();
    }

    private boolean shouldReport() {
        return iter % reportInterval == 0;
    }
}