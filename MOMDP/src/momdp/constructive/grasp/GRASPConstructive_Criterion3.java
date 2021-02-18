package momdp.constructive.grasp;

public class GRASPConstructive_Criterion3 extends GRASPConstructive {
    
    public GRASPConstructive_Criterion3(){
        super();
        minimize = true;
    }

    int func = 0;

    @Override
    protected void reset() {
        super.reset();
        func = 0;
    }

    @Override
    protected void objectiveFunction() {
        func++;
        int constructive = constructives[func%constructives.length];

        switch (constructive){
            case 0: maxSumFunction(); break;
            case 1: maxMinFunction(); break;
            case 2: maxMinSumFunction(); break;
            case 3: minDiffFunction(); break;
            case 4: minPCenterFunction(); break;
        }
    }
}
