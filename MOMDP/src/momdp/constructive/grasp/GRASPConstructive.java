package momdp.constructive.grasp;

import momdp.Main;
import momdp.constructive.IConstructive;
import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GRASPConstructive implements IConstructive {

    protected class Candidate{
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
    protected List<Candidate> candidates;
    protected float gmax;
    protected float gmin;
    protected Instance instance;
    protected boolean minimize = true;


    public void solve(Instance instance, int numSolutions){
        this.instance = instance;
        solElements=new ArrayList<>(instance.getNumNodesSol());
        candidates=new ArrayList<>(instance.getNumNodes());
        Random rnd= RandomManager.getRandom();
        float limit;
        int limitIndex=0;
        int selectedNode;
        for(int j=0;j<numSolutions;j++){
            //reset sol count
            selectedNode= rnd.nextInt(instance.getNumNodes());
            for (int i=0; i<instance.getNumNodes();i++) {
                if(i!=selectedNode){
                    candidates.add(new Candidate(i));
                }
            }
            solElements.add(selectedNode);
            while(solElements.size()<instance.getNumNodesSol()){

                objectiveFunction();

                candidates.sort((o1, o2) -> {
                    if(o1.getValue() < o2.getValue()) return -1;
                    if(o1.getValue() > o2.getValue()) return 1;
                    return 0;
                });
                gmax = candidates.get(candidates.size()-1).getValue();
                gmin = candidates.get(0).getValue();

                limit = minimize ? gmin+Main.alpha*(gmax-gmin) : gmax-Main.alpha*(gmax-gmin);
                for(int i=0; i<candidates.size();i++){
                    if((minimize && candidates.get(i).getValue()>limit)||(!minimize&&candidates.get(i).getValue()>=limit)){
                        limitIndex=i;
                        break;
                    }
                }
                int bound = minimize ? limitIndex : candidates.size() - limitIndex;
                int aux= rnd.nextInt(bound);
                selectedNode = minimize ? aux : limitIndex + aux;

                solElements.add(candidates.get(selectedNode).getElement());
                candidates.remove(selectedNode);
            }
            Pareto.add(new Solution(instance,solElements));
            reset();
        }
    }

    protected void reset(){
        solElements.clear();
        candidates.clear();
    }

    protected void objectiveFunction(){
        //calcular gmin
        //calcular gmax
        //recalcular valores de cada candidato
        //ordenar la lista de candidatos
    }

    protected void maxSumFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float maxSum = 0;
        int solElementsSize = solElements.size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=solElements.get(i);
            for(int j=0; j<solElementsSize;j++){
                nodeB=solElements.get(j);
                distance=instance.getDistances()[nodeA][nodeB];
                if(j>i){ //triangular, evitar pares repetidos
                    //maxSum
                    maxSum+=distance;
                }
            }
        }

        float sum = 0;
        Candidate candidate;
        int candidatesSize = candidates.size();
        for(int i = 0; i < candidatesSize; i++){
            sum = 0;
            candidate = candidates.get(i);
            for(int j = 0; j < solElementsSize; j++){
                sum+=instance.getDistances()[candidate.getElement()][solElements.get(j)];
            }
            candidate.setValue(maxSum+sum);
        }
    }

    protected void maxMinFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float maxMin = Float.MAX_VALUE;
        int solElementsSize = solElements.size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=solElements.get(i);
            for(int j=0; j<solElementsSize;j++){
                nodeB=solElements.get(j);
                distance=instance.getDistances()[nodeA][nodeB];
                if(j>i && distance < maxMin){ //triangular, evitar pares repetidos
                    maxMin = distance;
                }
            }
        }

        Candidate candidate;
        int candidatesSize = candidates.size();
        float oldMaxMin = maxMin;
        for(int i = 0; i < candidatesSize; i++){
            candidate = candidates.get(i);
            maxMin = oldMaxMin;
            for(int j = 0; j < solElementsSize; j++){
                distance = instance.getDistances()[candidate.getElement()][solElements.get(j)];
                if(distance < maxMin){
                    maxMin = distance;
                }
            }
            candidate.setValue(maxMin);
        }
    }

    protected void maxMinSumFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float maxMinSum = Float.MAX_VALUE;
        float sum = 0;
        int solElementsSize = solElements.size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=solElements.get(i);
            sum = 0;
            for(int j=0; j<solElementsSize;j++){
                nodeB=solElements.get(j);
                distance=instance.getDistances()[nodeA][nodeB];
                sum+= distance;
            }
            if(sum < maxMinSum) maxMinSum = sum;
        }


        Candidate candidate;
        float oldMaxMinSum = maxMinSum;
        int candidatesSize = candidates.size();
        for(int i = 0; i < candidatesSize; i++){
            sum = 0;
            maxMinSum = oldMaxMinSum;
            candidate = candidates.get(i);
            for(int j = 0; j < solElementsSize; j++){
                sum+=instance.getDistances()[candidate.getElement()][solElements.get(j)];
            }
            if(sum < maxMinSum) maxMinSum = sum;
            candidate.setValue(maxMinSum);
        }
    }

    protected void minDiffFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float max = Float.MAX_VALUE;
        float min = Float.MIN_VALUE;
        float sum = 0;
        int solElementsSize = solElements.size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=solElements.get(i);
            sum = 0;
            for(int j=0; j<solElementsSize;j++){
                nodeB=solElements.get(j);
                distance=instance.getDistances()[nodeA][nodeB];
                sum+= distance;
            }
            if(sum < max) max = sum;
            if(sum > min) min = sum;
        }


        Candidate candidate;
        float oldMax = max;
        float oldMin = min;
        float minDiff = 0f;
        int candidatesSize = candidates.size();
        for(int i = 0; i < candidatesSize; i++){
            sum = 0;
            max = oldMax;
            min = oldMin;
            candidate = candidates.get(i);
            for(int j = 0; j < solElementsSize; j++){
                sum+=instance.getDistances()[candidate.getElement()][solElements.get(j)];
            }
            if(sum < max) max = sum;
            if(sum > min) min = sum;
            minDiff = max-min;
            candidate.setValue(minDiff);
        }
    }

    protected void minPCenterFunction(){
        //Min P Center
        float minDist;
        int nodeA;
        int nodeB;
        float distance;
        float minPCenter = Float.MIN_VALUE;
        Candidate c;

        int solElementsSize = solElements.size();
        for (int k = 0; k < candidates.size(); k++) {
            c = candidates.get(k);
            candidates.remove(k);
            solElements.add(c.getElement());

            for(int i = 0; i < candidates.size(); i++){
                nodeA = candidates.get(i).getElement();
                minDist = Float.MAX_VALUE;
                for(int j = 0; j < solElements.size(); j++){
                    nodeB = solElements.get(j);
                    distance = instance.getDistances()[nodeA][nodeB];
                    if(distance < minDist) minDist = distance;
                }
                if(minDist > minPCenter) minPCenter = minDist;
            }
            candidates.add(c);
            solElements.remove(solElementsSize);
            c.setValue(minPCenter);
        }
    }

}
