package pl.edu.agh.ml.killing.state.partial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import pl.edu.agh.ml.killing.core.Position;
import pl.edu.agh.ml.killing.state.EntityInfo;
import pl.edu.agh.ml.killing.state.StateInfo;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public final class PlayerVicinity {

    private final EntityInfo player;
    private final ImmutableSet<EntityInfo> enemies;

    private PlayerVicinity(EntityInfo player, ImmutableSet<EntityInfo> enemies) {
        this.player = player;
        this.enemies = enemies;
    }

    public EntityInfo player() {
        return player;
    }

    public ImmutableSet<EntityInfo> enemies() {
        return enemies;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerVicinity) {
            PlayerVicinity other = (PlayerVicinity) obj;
            return player.equals(other.player) && enemies.equals(other.enemies);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, enemies);
    }

    public static Function<StateInfo, PlayerVicinity> withRadius(int radius) {
        return s -> from(s, radius);
    }

    public static PlayerVicinity from(StateInfo state, int radius) {
        EntityInfo player = state.player();
        Position origin = player.position();

        ImmutableSet.Builder<EntityInfo> translated = ImmutableSet.builder();
        state.enemies().stream()
                .filter(e -> dist(e, player) <= radius)
                .forEach(e -> translated.add(relative(e, origin)));

        return new PlayerVicinity(relative(player, origin), translated.build());
    }

    public static PlayerVicinity closest(StateInfo state, int k) {
        EntityInfo player = state.player();
        Position origin = player.position();

        List<EntityInfo> enemies = new ArrayList<>(state.enemies());
        enemies.sort((a, b) -> Integer.compare(dist(a, player), dist(b, player)));

        ImmutableSet.Builder<EntityInfo> translated = ImmutableSet.builder();
        Iterables.limit(enemies, k).forEach(e -> translated.add(relative(e, origin)));

        return new PlayerVicinity(relative(player, origin), translated.build());
    }

    public static Function<StateInfo, PlayerVicinity> closest(int k) {
        return info -> closest(info, k);
    }

    private static EntityInfo relative(EntityInfo entity, Position origin) {
        return new EntityInfo(entity.side(), entity.hp(), diff(entity.position(), origin));
    }

    private static Position diff(Position to, Position from) {
        return new Position(to.x - from.x, to.y - from.y);
    }

    private static int dist(EntityInfo a, EntityInfo b) {
        int dx = a.position().x - b.position().x;
        int dy = a.position().y - b.position().y;
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

}
