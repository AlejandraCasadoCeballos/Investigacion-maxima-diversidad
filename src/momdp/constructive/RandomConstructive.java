package momdp.constructive;

import momdp.Main;
import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.*;

public class RandomConstructive implements IConstructive {

    public String getName(){
        return "randomConstructive_"+ Main.seed;
    }

    public void solve(Instance instance, int numSolutions) {
        List<Integer> solElements = new ArrayList<>(instance.getNumNodesSol());
        Random rnd= RandomManager.getRandom();
        int numRandom;
        int element;
        List<Integer> allElements=new ArrayList<>(instance.getNumNodes());
        Solution sol;

        for(int j = 0; j < numSolutions; j++){
            for(int i=0;i<instance.getNumNodes();i++){
                allElements.add(i);
            }
            for(int i=0;i<instance.getNumNodesSol();i++){
                numRandom= rnd.nextInt(instance.getNumNodes()-i);
                element=allElements.get(numRandom);
                allElements.remove(numRandom);
                solElements.add(element);
            }
            Pareto.add(new Solution(instance, solElements));
            allElements.clear();
            solElements.clear();
        }
    }
}
