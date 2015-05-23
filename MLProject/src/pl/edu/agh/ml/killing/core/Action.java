package pl.edu.agh.ml.killing.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

public interface Action {

    public class Idle implements Action {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Idle;
        }

        @Override
        public int hashCode() {
            return Idle.class.hashCode();
        }

        @Override
        public String toString() {
            return "Idle";
        }

    }

    public class Move implements Action {

        private final Position destination;

        public Move(Position destination) {
            this.destination = checkNotNull(destination);
        }

        public Position destination() {
            return destination;
        }

        @Override
        public String toString() {
            return String.format("Move(to %s)", destination);
        }

        @Override
        public int hashCode() {
            return Objects.hash(1000, destination);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Move) {
                Move other = (Move) obj;
                return destination.equals(other.destination);
            } else {
                return false;
            }
        }
    }

    public class Attack implements Action {

        private final Entity target;

        public Attack(Entity target) {
            this.target = checkNotNull(target);
        }

        public Entity target() {
            return target;
        }

        @Override
        public String toString() {
            return String.format("Attack(target: %s)", target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(1001, target);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Attack) {
                Attack other = (Attack) obj;
                return target == other.target;
            } else {
                return false;
            }
        }
    }

}
