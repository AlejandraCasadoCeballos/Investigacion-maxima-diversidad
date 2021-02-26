package momdp.localSearch;

import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VNS{

    private float kMax = 30; //porcentajes
    private float kStep = 5;
    private final ILocalSearch localSearchObj;

    public VNS(ILocalSearch ls){
        localSearchObj=ls;
    }

    public void solve(Instance instance){
        kStep=(kStep/100)*instance.getNumNodesSol();
        kMax=(kMax/100)*instance.getNumNodesSol();

        float k = kStep;

        while(k < kMax){
            boolean shakeImprove=false;
            boolean lsImprove=false;
            List<Solution> frontCopy = Pareto.getFrontCopy();
            for(Solution sol : frontCopy){
                sol=shake(sol,k,instance);
                shakeImprove = shakeImprove || Pareto.add(sol);
                lsImprove = lsImprove || localSearchObj.localSearchSolution(sol);
            }
            if(lsImprove || shakeImprove) k=1;
            else k+=kStep;
        }
    }

    private Solution shake(Solution sol, float k, Instance instance){
        sol = sol.clone();
        Random rnd = RandomManager.getRandom();
        int numNodes = instance.getNumNodes();
        int numNodesSol = instance.getNumNodesSol();
        int diff = numNodes - numNodesSol;
        List<Integer> unselectedNodes = new ArrayList<>(diff);
        for(int i = 0; i < numNodes; i++){
            if(!sol.getElements().contains(i)){
                unselectedNodes.add(i);
            }
        }
        k=(int)Math.ceil(k);

        for(int i = 0; i < k; i++){
            int selected = rnd.nextInt(numNodesSol);
            int unselected = rnd.nextInt(diff-i);
            sol.getSolElements().remove(selected);
            sol.getSolElements().add(unselectedNodes.get(unselected));
            unselectedNodes.remove(unselected);
        }

        return sol;
    }

    public float getkMax() {
        return kMax;
    }
}
