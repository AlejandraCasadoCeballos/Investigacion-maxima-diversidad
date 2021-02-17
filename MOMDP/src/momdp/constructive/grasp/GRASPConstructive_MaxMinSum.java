package momdp.constructive.grasp;

public class GRASPConstructive_MaxMinSum extends GRASPConstructive {
    public GRASPConstructive_MaxMinSum(){
        minimize = false;
    }

    @Override
    protected void objectiveFunction() {
        maxMinSumFunction();
    }
}
