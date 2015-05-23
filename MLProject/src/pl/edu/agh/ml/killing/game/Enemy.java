package pl.edu.agh.ml.killing.game;

import static com.google.common.base.Preconditions.checkNotNull;
import pl.edu.agh.ml.killing.core.Entity;

class Enemy {

    private final Entity entity;

    private final EnemyAI ai;

    public Enemy(Entity entity, EnemyAI ai) {
        this.entity = checkNotNull(entity);
        this.ai = checkNotNull(ai);
    }

    public Entity entity() {
        return entity;
    }

    public EnemyAI ai() {
        return ai;
    }

}
