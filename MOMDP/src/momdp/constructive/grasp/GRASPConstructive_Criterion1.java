package momdp.constructive.grasp;

import momdp.Main;

public class GRASPConstructive_Criterion1 extends GRASPConstructive {
    int timesPerConstructive;


    public GRASPConstructive_Criterion1(){
        super();
        minimize = false;
        timesPerConstructive = Main.numSolutions / constructives.length;
    }

    @Override
    protected void objectiveFunction() {
        int constructive = constructives[solIndex/timesPerConstructive];
        switch (constructive){
            case 0: maxSumFunction(); break;
            case 1: maxMinFunction(); break;
            case 2: maxMinSumFunction(); break;
            case 3: minDiffFunction(); break;
            case 4: minPCenterFunction(); break;
        }
    }
}
