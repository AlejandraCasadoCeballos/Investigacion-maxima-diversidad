package momdp.algorithms;

import momdp.structure.Instance;

import java.util.*;

public class RandomSolver extends Solver { //esta clase se pasa a llamar randomconstructive

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

        System.out.println("Solution elements");
        for(int i : solElements){
            System.out.print(i + " ");
        }
        System.out.println();


        getMetrics();
    }
}
