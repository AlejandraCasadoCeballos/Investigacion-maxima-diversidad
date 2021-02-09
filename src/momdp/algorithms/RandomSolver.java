package momdp.algorithms;

import momdp.structure.Instance;

import java.util.*;

public class RandomSolver extends Solver {

    @Override
    public void solve(Instance instance) {
        super.solve(instance);
        Random rnd=new Random();
        List<Integer> allElements=new ArrayList<>(numNodes);
        for(int i=0;i<numNodes;i++){
            allElements.add(i);
        }
        int numRandom;
        int element;
        for(int i=0;i<numNodesSol;i++){
            numRandom= rnd.nextInt(numNodes-i);
            element=allElements.get(numRandom);
            allElements.remove(numRandom);
            solElements.add(element);
        }
        getMetrics();
    }
}
