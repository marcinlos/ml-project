package pl.edu.agh.ml.killing.app;

public class Params {

    public final GameParams game = new GameParams();

    public final TestParams test = new TestParams();

    public final StateParams state = new StateParams();

    public final SupervisedLearningParams supervised = new SupervisedLearningParams();

    public final QLearningParams reinforced = new QLearningParams();

    public Object[] all() {
        return new Object[] {
                game,
                test,
                state,
                supervised,
                reinforced
        };
    }
}
