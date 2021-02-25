package momdp.localSearch;

import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VNS{

    public final int kMax = 5;
    private final ILocalSearch localSearchObj;


    public VNS(ILocalSearch ls){
        localSearchObj=ls;
    }

    public void solve(){
        int k = 1;

        while(k < kMax){
            boolean shakeImprove=false;
            boolean lsImprove=false;
            for(Solution sol : Pareto.getFront()){
                sol=shake(sol,k);
                shakeImprove = shakeImprove || Pareto.add(sol);
                lsImprove = lsImprove || localSearchObj.localSearchSolution(sol);
            }
            if(lsImprove || shakeImprove) k=1;
            else k++;
        }
    }

    private Solution shake(Solution sol, int k){
        sol = sol.clone();
        Random rnd = RandomManager.getRandom();
        int numNodes = sol.getInstance().getNumNodes();
        int numNodesSol = sol.getInstance().getNumNodesSol();
        int diff = numNodes - numNodesSol;
        List<Integer> nonSelected = new ArrayList<>(diff);
        for(int i = 0; i < numNodes; i++){
            if(!sol.getElements().contains(i)){
                nonSelected.add(i);
            }
        }

        int max = Math.min(diff, k);

        for(int i = 0; i < max; i++){
            int selected = rnd.nextInt(numNodesSol);
            int unselected = rnd.nextInt(numNodes-numNodesSol-i);
            sol.getSolElements().remove(selected);
            sol.getSolElements().add(nonSelected.get(unselected));
            nonSelected.remove(unselected);
        }

        return sol;
    }

}
