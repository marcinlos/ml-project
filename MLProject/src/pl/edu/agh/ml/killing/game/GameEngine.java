package pl.edu.agh.ml.killing.game;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import pl.edu.agh.ml.killing.core.Action;
import pl.edu.agh.ml.killing.core.Direction;
import pl.edu.agh.ml.killing.core.Entity;
import pl.edu.agh.ml.killing.core.Position;
import pl.edu.agh.ml.killing.core.Result;
import pl.edu.agh.ml.killing.state.EntityInfo;
import pl.edu.agh.ml.killing.state.GameState;

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

    public static GameEngine create(GameConfig config) {
        BattleBuilder builder = new BattleBuilder(config);

        Battlefield battlefield = builder.newBattlefield();
        Entity player = builder.addPlayer(battlefield);
        Set<Enemy> enemies = builder.createEnemies(battlefield);
        return new GameEngine(battlefield, player, enemies);
    }

    private GameEngine(Battlefield battlefield, Entity player, Collection<Enemy> enemies) {
        this.battlefield = battlefield;
        this.player = player;
        this.entityToEnemy = buildMap(enemies);
        this.enemies = entityToEnemy.values();
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

        forEachNeighbour(entity.position(), (dir, p) -> {
            battlefield.get(p)
                    .map(e -> tryAttack(entity, e, dir))
                    .orElse(Optional.of(new Action.Move(dir)))
                    .ifPresent(actions::add);
        });
        return actions.build();
    }

    public ImmutableSet<Action> availableActions() {
        return availableActions(player);
    }

    private Optional<Action> tryAttack(Entity aggressor, Entity target, Direction dir) {
        return aggressor.side() == target.side() ?
                Optional.empty() :
                Optional.of(new Action.Attack(dir));
    }

    private void forEachNeighbour(Position pos, BiConsumer<Direction, Position> f) {
        for (Direction d : Direction.values()) {
            Position p = pos.move(d);
            if (battlefield.extent().inRange(p)) {
                f.accept(d, p);
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
        Position dest = entity.position().move(move.direction());
        battlefield.move(entity, dest);
    }

    private void performAttack(Entity entity, Action.Attack attack) {
        Position location = entity.position().move(attack.direction());
        battlefield.get(location).ifPresent(target -> {
            target.hurt(1);
            if (target.isDead()) {
                remove(target);
            }
        });
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

    public GameState snapshot() {
        return GameState.builder()
                .setPlayer(player)
                .setMapExtent(battlefield.extent())
                .addAll(entityToEnemy.keySet())
                .build();
    }

    public static GameEngine load(GameConfig config, GameState state) {
        Battlefield battlefield = new Battlefield(state.mapExtent());
        Entity player = entityFromInfo(state.player());

        List<Enemy> enemies = state.enemies().stream()
                .map(info -> new Enemy(entityFromInfo(info), config.newAI()))
                .collect(Collectors.toList());

        enemies.forEach(e -> battlefield.add(e.entity()));
        return new GameEngine(battlefield, player, enemies);
    }

    private static Entity entityFromInfo(EntityInfo info) {
        return new Entity(info.side(), info.hp(), info.position());
    }
}
