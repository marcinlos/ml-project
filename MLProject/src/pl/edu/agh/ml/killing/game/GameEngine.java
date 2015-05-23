package pl.edu.agh.ml.killing.game;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Direction;
import pl.edu.agh.ml.killing.core.Entity;
import pl.edu.agh.ml.killing.core.Position;
import pl.edu.agh.ml.killing.core.Result;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class GameEngine {

    private final Entity player;
    private final Battlefield battlefield;
    private final BiMap<Entity, Enemy> entityToEnemy;
    private final Set<Enemy> enemies;

    private final HandlerMap<Action> handlers = HandlerMap.<Action> builder()
            .put(Action.Idle.class, this::performIdle)
            .put(Action.Move.class, this::performMove)
            .put(Action.Attack.class, this::performAttack)
            .build();

    public GameEngine(GameConfig config) {
        BattleBuilder builder = new BattleBuilder(config);

        battlefield = builder.newBattlefield();
        player = builder.addPlayer(battlefield);
        entityToEnemy = buildMap(builder.createEnemies(battlefield));
        enemies = entityToEnemy.values();
    }

    private static BiMap<Entity, Enemy> buildMap(Collection<Enemy> enemies) {
        BiMap<Entity, Enemy> map = HashBiMap.create(enemies.size());
        enemies.forEach(enemy -> map.put(enemy.entity(), enemy));
        return map;
    }

    public void playRound(Action action) {
        checkState(!finished());
        act(player, action);

        ImmutableList.copyOf(enemies).forEach(enemy -> {
            if (enemy.entity().isAlive()) {
                playEnemy(enemy);
            } else {
                remove(enemy);
            }
        });
    }

    private void playEnemy(Enemy enemy) {
        Entity entity = enemy.entity();
        Action action = enemy.ai().chooseAction(entity, player, availableActions(entity));
        act(entity, action);
    }

    private void remove(Enemy enemy) {
        remove(enemy.entity());
    }

    private void remove(Entity entity) {
        entityToEnemy.remove(entity);
        battlefield.remove(entity.position());
    }

    private ImmutableSet<Action> availableActions(Entity entity) {
        ImmutableSet.Builder<Action> actions = ImmutableSet.builder();
        actions.add(new Action.Idle());

        forEachNeighbour(entity.position(), p -> {
            battlefield.get(p)
                    .map(e -> tryAttack(entity, e))
                    .orElse(Optional.of(new Action.Move(p)))
                    .ifPresent(actions::add);
        });
        return actions.build();
    }

    public ImmutableSet<Action> availablePlayerActions() {
        return availableActions(player);
    }

    private Optional<Action> tryAttack(Entity aggressor, Entity target) {
        return aggressor.side() == target.side() ?
                Optional.empty() :
                Optional.of(new Action.Attack(target));
    }

    private void forEachNeighbour(Position pos, Consumer<Position> f) {
        for (Direction d : Direction.values()) {
            Position p = pos.move(d);
            if (battlefield.extent().inRange(p)) {
                f.accept(p);
            }
        }
    }

    private void act(Entity entity, Action action) {
        handlers.handle(entity, action);
    }

    private void performIdle(Entity entity, Action.Idle idle) {
        // empty
    }

    private void performMove(Entity entity, Action.Move move) {
        battlefield.move(entity, move.destination());
    }

    private void performAttack(Entity entity, Action.Attack attack) {
        Entity target = attack.target();
        checkState(dist(entity, target) <= 1, "Target outside range");
        target.hurt(1);
        if (target.isDead()) {
            remove(target);
        }
    }

    private static int dist(Entity a, Entity b) {
        int dx = Math.abs(a.position().x - b.position().x);
        int dy = Math.abs(a.position().y - b.position().y);
        return Math.max(dx, dy);
    }

    public Optional<Result> result() {
        if (player.isDead()) {
            return Optional.of(Result.LOST);
        } else if (entityToEnemy.isEmpty()) {
            return Optional.of(Result.WON);
        } else {
            return Optional.empty();
        }
    }

    public boolean finished() {
        return result().isPresent();
    }

}
