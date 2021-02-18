package momdp.constructive.grasp;

public class GRASPConstructive_Criterion2 extends GRASPConstructive {
    public GRASPConstructive_Criterion2(){
        super();
        minimize = true;
    }

    @Override
    protected void objectiveFunction() {
        maxMinFunction();
    }
}
