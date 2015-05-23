package pl.edu.agh.ml.killing.game;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Entity;

import com.google.common.collect.ImmutableSet;

public interface EnemyAI {

    Action chooseAction(Entity entity, Entity player, ImmutableSet<Action> availableActions);

}
