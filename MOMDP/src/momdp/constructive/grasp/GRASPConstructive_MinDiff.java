package momdp.constructive.grasp;

public class GRASPConstructive_MinDiff extends GRASPConstructive {
    public GRASPConstructive_MinDiff(){
        minimize = true;
    }

    @Override
    protected void objectiveFunction() {
        minDiffFunction();
    }
}
