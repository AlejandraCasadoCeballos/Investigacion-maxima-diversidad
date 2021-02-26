package momdp.localSearch;

import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VNS{

    private final float kMax = 30; //porcentajes
    private final float kStep = 5;
    private final ILocalSearch localSearchObj;

    public VNS(ILocalSearch ls){
        localSearchObj=ls;
    }

    public void solve(Instance instance){
        float kStepLocal=(kStep/100)*instance.getNumNodesSol();
        float kMaxLocal=(kMax/100)*instance.getNumNodesSol();

        float k = kStepLocal;

        while(k < kMaxLocal){
            boolean shakeImprove=false;
            boolean lsImprove=false;
            List<Solution> frontCopy = Pareto.getFrontCopy();
            for(Solution sol : frontCopy){
                sol=shake(sol,k,instance);
                shakeImprove = shakeImprove || Pareto.add(sol);
                lsImprove = lsImprove || localSearchObj.localSearchSolution(sol);
            }
            if(lsImprove || shakeImprove) k=1;
            else k+=kStepLocal;
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
        k=Math.min((int)Math.ceil(k),diff);


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

    public float getkStep() {
        return kStep;
    }
}
