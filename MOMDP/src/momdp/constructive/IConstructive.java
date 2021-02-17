package momdp.constructive;

import momdp.Main;
import momdp.structure.Instance;

public interface IConstructive {
    void solve(Instance instance, int numSolutions);
    default String getName(){
        return getClass().getSimpleName()+"_Seed_"+ Main.seed+"_SolCount_"+Main.numSolutions;
    }
}
