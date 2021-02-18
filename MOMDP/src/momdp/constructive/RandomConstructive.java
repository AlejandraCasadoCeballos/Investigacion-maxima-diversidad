package momdp.constructive;

import momdp.Main;
import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.*;

public class RandomConstructive implements IConstructive {

    public void solve(Instance instance, int numSolutions) {
        //List<Integer> solElements = new ArrayList<>(instance.getNumNodesSol());
        Solution sol;
        Random rnd= RandomManager.getRandom();
        int numRandom;
        int element;
        List<Integer> allElements=new ArrayList<>(instance.getNumNodes());

        for(int j = 0; j < numSolutions; j++){
            sol = new Solution(instance);
            for(int i=0;i<instance.getNumNodes();i++){
                allElements.add(i);
            }
            for(int i=0;i<instance.getNumNodesSol();i++){
                numRandom= rnd.nextInt(instance.getNumNodes()-i);
                element=allElements.get(numRandom);
                allElements.remove(numRandom);
                sol.getElements().add(element);
            }
            Pareto.add(sol);
            allElements.clear();
        }
    }
}
