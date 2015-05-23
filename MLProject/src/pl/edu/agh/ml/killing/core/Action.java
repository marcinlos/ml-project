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

        private final Direction direction;

        public Move(Direction direction) {
            this.direction = checkNotNull(direction);
        }

        public Direction direction() {
            return direction;
        }

        @Override
        public String toString() {
            return String.format("Move(to %s)", direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(1000, direction);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Move) {
                Move other = (Move) obj;
                return direction == other.direction;
            } else {
                return false;
            }
        }
    }

    public class Attack implements Action {

        private final Direction direction;

        public Attack(Direction direction) {
            this.direction = checkNotNull(direction);
        }

        public Direction direction() {
            return direction;
        }

        @Override
        public String toString() {
            return String.format("Attack(to %s)", direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(1001, direction);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Attack) {
                Attack other = (Attack) obj;
                return direction == other.direction;
            } else {
                return false;
            }
        }
    }

}
