package momdp.localSearch;

import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.*;

public class LS_Swap implements ILocalSearch {

    public static int selectedPct = 100;
    public static int unselectedPct = 100;

    public String getName(){
        return getClass().getSimpleName() + "_"+selectedPct+"%_"+unselectedPct+"%";
    }

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
                unselectedNodes.add(new ElemDist(i, minDistToSelected(i, sol.getSolElements(), instance)));
            }
        }
        Collections.sort(unselectedNodes, (a,b)->Float.compare(b.dist, a.dist));
        List<ElemDist> selectedNodes = new ArrayList<>(numNodesSol);
        for(int i : sol.getSolElements()){
            selectedNodes.add(new ElemDist(i, maxDistToSelected(i, sol.getSolElements(), instance)));
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
        return anyImprovement;
    }

    private static class ElemDist{
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
