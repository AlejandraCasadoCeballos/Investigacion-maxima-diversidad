package momdp.constructive.grasp;

import momdp.Main;

public class GRASPConstructive_MaxSum extends GRASPConstructive {
    public GRASPConstructive_MaxSum(){
        minimize = false;
    }

    @Override
    protected void objectiveFunction() {
        maxSumFunction();
    }
}
