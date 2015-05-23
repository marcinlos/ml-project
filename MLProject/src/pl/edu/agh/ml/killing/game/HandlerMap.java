package pl.edu.agh.ml.killing.game;

import java.util.function.BiConsumer;

import pl.edu.agh.ml.killing.core.Entity;

import com.google.common.collect.ImmutableMap;

class HandlerMap<S> {

    private final ImmutableMap<Class<? extends S>, BiConsumer<Entity, ? extends S>> handlers;

    private HandlerMap(ImmutableMap<Class<? extends S>, BiConsumer<Entity, ? extends S>> handlers) {
        this.handlers = handlers;
    }

    public static <T> HandlerMapBuilder<T> builder() {
        return new HandlerMapBuilder<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends S> BiConsumer<Entity, T> get(Class<T> klass) {
        return (BiConsumer<Entity, T>) handlers.get(klass);
    }

    public <T extends S> void handle(Entity entity, T value) {
        @SuppressWarnings("unchecked")
        Class<T> klass = (Class<T>) value.getClass();
        get(klass).accept(entity, value);
    }

    public static class HandlerMapBuilder<S> {

        private final ImmutableMap.Builder<Class<? extends S>, BiConsumer<Entity, ? extends S>> builder = ImmutableMap
                .builder();

        public <T extends S> HandlerMapBuilder<S> put(Class<T> klass, BiConsumer<Entity, T> f) {
            builder.put(klass, f);
            return this;
        }

        public HandlerMap<S> build() {
            return new HandlerMap<>(builder.build());
        }
    }
}