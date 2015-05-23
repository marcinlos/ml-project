package pl.edu.agh.ml.killing.state;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import pl.edu.agh.ml.killing.core.Entity;
import pl.edu.agh.ml.killing.core.MapExtent;
import pl.edu.agh.ml.killing.core.Side;

import com.google.common.collect.ImmutableSet;

public final class StateInfo {

    private final EntityInfo player;
    private final ImmutableSet<EntityInfo> enemies;
    private final MapExtent mapExtent;

    private StateInfo(EntityInfo player, ImmutableSet<EntityInfo> enemies, MapExtent mapExtent) {
        this.player = player;
        this.enemies = enemies;
        this.mapExtent = mapExtent;
    }

    public EntityInfo player() {
        return player;
    }

    public ImmutableSet<EntityInfo> enemies() {
        return enemies;
    }

    public MapExtent mapExtent() {
        return mapExtent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StateInfo) {
            StateInfo other = (StateInfo) obj;
            return player.equals(other.player)
                    && enemies.equals(other.enemies)
                    && mapExtent.equals(other.mapExtent);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, enemies, mapExtent);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private EntityInfo player;
        private final ImmutableSet.Builder<EntityInfo> enemies = ImmutableSet.builder();
        private MapExtent mapExtent;

        public Builder setPlayer(Entity player) {
            checkArgument(player.side() == Side.PLAYER, "Player should be on player's side");
            this.player = EntityInfo.from(player);
            return this;
        }

        public Builder setMapExtent(MapExtent mapExtent) {
            this.mapExtent = checkNotNull(mapExtent);
            return this;
        }

        public Builder add(Entity entity) {
            checkArgument(entity.side() == Side.ENEMY, "Enemies should be on enemy's side");
            this.enemies.add(EntityInfo.from(entity));
            return this;
        }

        public Builder addAll(Iterable<Entity> enemies) {
            enemies.forEach(this::add);
            return this;
        }

        public StateInfo build() {
            checkNotNull(player, "Player data has not been set");
            checkNotNull(mapExtent, "Map extent has not been set");

            return new StateInfo(player, enemies.build(), mapExtent);
        }
    }

}
