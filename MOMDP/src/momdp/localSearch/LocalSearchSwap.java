package momdp.localSearch;

import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.Solution;

import java.util.List;

public class LocalSearchSwap {

    private boolean firstImprovement=true;


    public void localSearchSolution(Solution sol){
        sol=sol.clone();
        boolean improvement=true;
        Instance instance=sol.getInstance();
        int numNodesSol=instance.getNumNodesSol();
        int numNodes=instance.getNumNodes();
        List<Integer> solElements=sol.getSolElements();
        int aux;

        while(improvement){
            improvement=false;
            for(int i=0;i<numNodesSol;i++){
                for(int j=0; j<numNodes; j++){
                    if(!solElements.contains(j)){
                        aux=solElements.get(i);
                        solElements.remove(i);
                        solElements.add(j);
                        if(Pareto.add(sol)){
                            sol=sol.clone();
                            improvement=true;
                            break;
                        }
                        else{
                            solElements.remove(numNodesSol-1);
                            solElements.add(aux);
                        }
                    }
                }
                if(improvement) break;

            }
        }


    }



}
