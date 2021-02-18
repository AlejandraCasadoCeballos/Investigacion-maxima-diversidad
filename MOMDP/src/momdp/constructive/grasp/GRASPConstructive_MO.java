package momdp.constructive.grasp;

public class GRASPConstructive_MO extends GRASPConstructive {
    
    public GRASPConstructive_MO(){
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

        switch (func){
            case 0: maxSumFunction(); break;
            case 1: maxMinFunction(); break;
            case 2: maxMinSumFunction(); break;
            case 3: minDiffFunction(); break;
            case 4: minPCenterFunction(); break;
        }

        func = (func+1)%5;
        //func = (func+1)%4;
    }
}
