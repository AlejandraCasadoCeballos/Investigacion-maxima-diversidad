package momdp.constructive.grasp;

public class GRASPConstructive_MinPCenter extends GRASPConstructive {
    public GRASPConstructive_MinPCenter(){
        minimize = true;
    }

    @Override
    protected void objectiveFunction() {
        maxMinFunction();
    }
}
