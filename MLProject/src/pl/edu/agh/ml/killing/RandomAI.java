package pl.edu.agh.ml.killing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Entity;
import pl.edu.agh.ml.killing.game.EnemyAI;

import com.google.common.collect.ImmutableSet;

public class RandomAI implements EnemyAI {

    @Override
    public Action chooseAction(Entity entity, Entity player, ImmutableSet<Action> availableActions) {
        List<Action> actions = new ArrayList<>(availableActions);
        Collections.shuffle(actions);
        return actions.get(0);
    }

}
