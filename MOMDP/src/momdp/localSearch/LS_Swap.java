package momdp.localSearch;

import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.*;

public class LS_Swap implements ILocalSearch {

    public boolean localSearchSolution(Solution sol){
        sol=sol.clone();
        Instance instance=sol.getInstance();
        int numNodesSol=instance.getNumNodesSol();
        int numNodes=instance.getNumNodes();

        Queue<Solution> solutionsToImprove = new ArrayDeque<>();
        solutionsToImprove.add(sol);

        boolean anyImprovement = false;

        int diff = numNodes-numNodesSol;
        List<Integer> unselectedNodes = new ArrayList<>(diff);
        for(int i = 0; i < numNodes; i++){
            if(!sol.getElements().contains(i)){
                unselectedNodes.add(i);
            }
        }
        Random rnd = RandomManager.getRandom();
        Collections.shuffle(unselectedNodes, rnd);
        Collections.shuffle(sol.getSolElements(), rnd);

        while(!solutionsToImprove.isEmpty()){ //Mientras haya soluciones parciales a mejorar
            boolean improvement=false;
            sol = solutionsToImprove.remove();
            for(int i=0;i<numNodesSol;i++){
                for(int j=0; j<diff; j++){
                    int unselectedNode = unselectedNodes.remove(j);
                    int selectedNode = sol.getSolElements().remove(i);
                    unselectedNodes.add(selectedNode);
                    sol.getSolElements().add(unselectedNode);

                    if(Pareto.add(sol)){
                        solutionsToImprove.add(sol.clone());
                        improvement=true;
                        anyImprovement = true;
                        if(firstImprovement) break;
                    }
                    if(!firstImprovement){
                        sol.getSolElements().remove(numNodesSol-1);
                        sol.getSolElements().add(selectedNode);
                        unselectedNodes.remove(diff-1);
                        unselectedNodes.add(unselectedNode);
                    }
                }
                if(firstImprovement && improvement) break;
            }
        }
        return anyImprovement;
    }
}
