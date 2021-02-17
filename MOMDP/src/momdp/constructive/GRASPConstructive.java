package momdp.constructive;

import momdp.Main;
import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GRASPConstructive implements IConstructive{

    private class Candidate{
        private int element;
        private float value;

        public Candidate(int element){
            this.element=element;
        }

        public int getElement() {
            return element;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }

    protected List<Integer> solElements;
    protected float gmax;
    protected float gmin;

    public String getName(){
        return "GRASP_Seed_"+ Main.seed+"_SolCount_"+Main.numSolutions;
    }
    public void solve(Instance instance, int numSolutions){
        solElements=new ArrayList<>(instance.getNumNodesSol());
        List<Candidate> candidates=new ArrayList<>(instance.getNumNodes());
        Random rnd= RandomManager.getRandom();
        float limit;
        int limitIndex=0;
        int selectedNode;
        for(int j=0;j<numSolutions;j++){
            selectedNode= rnd.nextInt(instance.getNumNodes());
            for (int i=0; i<instance.getNumNodes();i++) {
                if(i!=selectedNode){
                    candidates.add(new Candidate(i));
                }
            }
            solElements.add(selectedNode);
            while(solElements.size()<instance.getNumNodesSol()){
                //llamada al calculo de la funcion objetivo de cada elemento
                limit=gmax-Main.alpha*(gmax-gmin);
                for(int i=0; i<candidates.size();i++){
                    if(candidates.get(i).getValue()>limit){
                        limitIndex=i;
                        break;
                    }
                }
                selectedNode=rnd.nextInt(limitIndex);
                solElements.add(candidates.get(selectedNode).getElement());
                candidates.remove(selectedNode);
            }
            Pareto.add(new Solution(instance,solElements));
            solElements.clear();
            candidates.clear();
        }

    }

    protected void objectiveFunction(){

    }


}
