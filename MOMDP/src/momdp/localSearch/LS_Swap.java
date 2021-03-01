package momdp.localSearch;

import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.*;

public class LS_Swap implements ILocalSearch {

    private final int selectedPct = 50;
    private final int unselectedPct = 50;

    public boolean localSearchSolution(Solution sol){
        sol=sol.clone();
        Instance instance=sol.getInstance();
        int numNodesSol=instance.getNumNodesSol();
        int numNodes=instance.getNumNodes();

        //Queue<Solution> solutionsToImprove = new ArrayDeque<>();
        //solutionsToImprove.add(sol);

        boolean anyImprovement = false;
        boolean improvement = true;

        //Crear listas ordenadas de selccionados y no seleccionados
        int diff = numNodes-numNodesSol;
        List<ElemDist> unselectedNodes = new ArrayList<>(diff);
        for(int i = 0; i < numNodes; i++){
            if(!sol.getElements().contains(i)){
                unselectedNodes.add(new ElemDist(i,minDistToSelected(i, sol.getSolElements(), instance)));
            }
        }
        Collections.sort(unselectedNodes, (a,b)->Float.compare(b.dist, a.dist));
        List<ElemDist> selectedNodes = new ArrayList<>(numNodesSol);
        for(int i : sol.getSolElements()){
            selectedNodes.add(new ElemDist(i, maxDistToSelected(i,sol.getSolElements(),instance)));
        }
        Collections.sort(selectedNodes, (a,b)->Float.compare(a.dist, b.dist));

        //max indexes
        int maxI = (int)(selectedPct/100.0 * numNodesSol);
        int maxJ = (int)(unselectedPct/100.0 * diff);

        //first improvement
        while(improvement){
            improvement = false;
            for(int i = 0; i < maxI;i++){
                for(int j= 0; j < maxJ; j++){
                    ElemDist selected = selectedNodes.remove(i);
                    ElemDist unselected = unselectedNodes.remove(j);

                    sol.getSolElements().remove(Integer.valueOf(selected.node));
                    sol.getSolElements().add(unselected.node);

                    if(Pareto.add(sol)){
                        selectedNodes.add(unselected);
                        unselectedNodes.add(selected);
                        anyImprovement = true;
                        improvement = true;
                        break;
                    } else {
                        selectedNodes.add(selected);
                        unselectedNodes.add(unselected);
                        sol.getSolElements().remove(numNodesSol-1);
                        sol.getSolElements().add(selected.node);
                    }

                }
                if(improvement) break;
            }
        }

        /*while(!solutionsToImprove.isEmpty()){ //Mientras haya soluciones parciales a mejorar
            boolean improvement=false;
            sol = solutionsToImprove.remove();
            for(int i=0;i<numNodesSol;i++){
                for(int j=0; j<diff; j++){
                    ElemDist unselectedNode = unselectedNodes.remove(j);
                    ElemDist selectedNode = selectedNodes.remove(i);
                    sol.getSolElements().remove(Integer.valueOf(selectedNode.node));
                    sol.getSolElements().add(unselectedNode.node);

                    unselectedNodes.add(selectedNode);

                    if(Pareto.add(sol)){
                        solutionsToImprove.add(sol.clone());
                        improvement=true;
                        anyImprovement = true;
                        selectedNodes.add(unselectedNode);
                        if(firstImprovement) break;
                    } else {
                        selectedNodes.add(selectedNode);
                    }
                    sol.getSolElements().remove(numNodesSol-1);
                    sol.getSolElements().add(selectedNode.node);
                    if(!firstImprovement){

                        selectedNodes.add(selectedNode);
                        unselectedNodes.remove(diff-1);
                        unselectedNodes.add(unselectedNode);
                    }
                }
                if(firstImprovement && improvement) break;
            }
        }*/
        return anyImprovement;
    }

    private class ElemDist{
        public int node;
        public float dist;

        public ElemDist(int node, float dist){
            this.node = node;
            this.dist = dist;
        }
    }

    private float minDistToSelected(int node, List<Integer> selected, Instance instance){
        float min = 0x3f3f3f;
        for(int other : selected){
            float dist = instance.getDistances()[node][other];
            if(dist < min) min = dist;
        }
        return min;
    }

    private float maxDistToSelected(int node, List<Integer> selected, Instance instance){
        float max = 0;
        for(int other : selected){
            float dist = instance.getDistances()[node][other];
            if(dist > max) max = dist;
        }
        return max;
    }
}
