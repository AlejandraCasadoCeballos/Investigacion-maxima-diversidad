package momdp.constructive.grasp;

import momdp.Main;

public class GRASPConstructive_MaxMin extends GRASPConstructive {
    public GRASPConstructive_MaxMin(){
        minimize = false;
    }

    @Override
    protected void objectiveFunction() {
        maxMinFunction();
    }
}
