package momdp.constructive;

import momdp.structure.Instance;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.*;

public class RandomConstructive implements IConstructive {

    public void solve(Instance instance) {
        List<Integer> solElements = new ArrayList<>();

        RandomManager.setSeed(13);
        Random rnd= RandomManager.getRandom();

        List<Integer> allElements=new ArrayList<>(instance.getNumNodes());
        for(int i=0;i<instance.getNumNodes();i++){
            allElements.add(i);
        }
        int numRandom;
        int element;
        for(int i=0;i<instance.getNumNodesSol();i++){
            numRandom= rnd.nextInt(instance.getNumNodes()-i);
            element=allElements.get(numRandom);
            allElements.remove(numRandom);
            solElements.add(element);
        }


        Solution sol = new Solution(instance, solElements);
    }
}
