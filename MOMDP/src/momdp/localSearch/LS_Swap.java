package momdp.localSearch;

import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.Solution;

import java.util.ArrayDeque;
import java.util.Queue;

public class LS_Swap implements ILocalSearch {

    public boolean localSearchSolution(Solution sol){
        sol=sol.clone();
        Instance instance=sol.getInstance();
        int numNodesSol=instance.getNumNodesSol();
        int numNodes=instance.getNumNodes();

        Queue<Solution> solutionsToImprove = new ArrayDeque<>();
        solutionsToImprove.add(sol);

        boolean anyImprovement = false;

        //TODO:HACER SHUFFLE DE LOS ELEMENTOS DE LA LISTA DE SELECCIONADOS Y NO SELECCIONADOS

        while(!solutionsToImprove.isEmpty()){ //Mientras haya soluciones parciales a mejorar
            boolean improvement=false;
            sol = solutionsToImprove.remove();
            for(int i=0;i<numNodesSol;i++){
                for(int j=0; j<numNodes; j++){
                    if(!sol.getSolElements().contains(j)){
                        int aux = sol.getSolElements().remove(i);
                        sol.getSolElements().add(j);
                        if(Pareto.add(sol)){
                            solutionsToImprove.add(sol.clone());
                            improvement=true;
                            anyImprovement = true;
                            if(firstImprovement) break;
                        }
                        if(!firstImprovement){
                            sol.getSolElements().remove(numNodesSol-1);
                            sol.getSolElements().add(aux);
                        }
                    }
                }
                if(firstImprovement && improvement) break;
            }
        }
        return anyImprovement;
    }
}
