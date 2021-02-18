package momdp.constructive.grasp;

import momdp.Main;
import momdp.constructive.IConstructive;
import momdp.structure.*;

import java.util.*;

public abstract class GRASPConstructive implements IConstructive {

    protected class Candidate{
        public int element;
        public float value;

        public Candidate(int element){
            this.element=element;
        }
    }

    //protected List<Integer> solElements;
    protected List<Candidate> candidates;
    protected float gmax;
    protected float gmin;
    protected Instance instance;
    protected boolean minimize = true;
    protected Solution sol;
    protected int solIndex = 0;
    protected int[] constructives = new int[]{0,1,2,3};

    private int[] rcl;

    public void solve(Instance instance, int numSolutions){
        this.instance = instance;
        rcl = new int[instance.getNumNodes()];
        candidates=new ArrayList<>(instance.getNumNodes());
        Random rnd= RandomManager.getRandom();


        int limitIndex=0;
        int selectedNode;
        Candidate removedCandidate;

        //Solution sol;
        for(solIndex=0;solIndex<numSolutions;solIndex++){
            float alpha = Main.getAlpha();

            //reset sol count
            sol = new Solution(instance);
            selectedNode= rnd.nextInt(instance.getNumNodes());


            for (int i=0; i<instance.getNumNodes();i++) {
                if(i!=selectedNode){
                    candidates.add(new Candidate(i));
                }
            }

            sol.getElements().add(selectedNode);
            while(sol.getElements().size()<instance.getNumNodesSol()){
                gmin = 0x3f3f3f;
                gmax = 0;
                objectiveFunction();
                limitIndex = findLimitIndexPerformant(alpha);


                //Select node performant
                selectedNode = rcl[rnd.nextInt(limitIndex)];

                //Add to solution
                removedCandidate = candidates.get(selectedNode);
                sol.getElements().add(removedCandidate.element);
                candidates.remove(selectedNode);
            }
            Pareto.add(sol);
            reset();
        }
    }

    private void setCandidateValue(Candidate candidate, float value){
        candidate.value = value;
        if(value > gmax) gmax = value;
        if(value < gmin) gmin = value;
    }

    private int findLimitIndexPerformant(float alpha){
        int limitIndex = 0;
        //maxLimit = 0;

        int candidatesSize = candidates.size();
        float limit = minimize ? gmin+alpha*(gmax-gmin) : gmax-alpha*(gmax-gmin);
        for(int i = 0; i < candidatesSize; i++){
            if((minimize && candidates.get(i).value<=limit)||(!minimize&&candidates.get(i).value>=limit)){
                rcl[limitIndex] = i;
                limitIndex++;
            }
        }
        return limitIndex;
    }

    protected void reset(){
        candidates.clear();
    }

    protected abstract void objectiveFunction();

    protected void maxSumFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float maxSum = 0;
        int solElementsSize = sol.getElements().size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=sol.getElements().get(i);
            for(int j=0; j<solElementsSize;j++){
                nodeB=sol.getElements().get(j);
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
                sum+=instance.getDistances()[candidate.element][sol.getElements().get(j)];
            }
            setCandidateValue(candidate, maxSum+sum);
        }
    }

    protected void maxMinFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float maxMin = 0x3f3f3f;
        int solElementsSize = sol.getElements().size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=sol.getElements().get(i);
            for(int j=0; j<solElementsSize;j++){
                nodeB=sol.getElements().get(j);
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
                distance = instance.getDistances()[candidate.element][sol.getElements().get(j)];
                if(distance < maxMin){
                    maxMin = distance;
                }
            }
            setCandidateValue(candidate, maxMin);
        }
    }

    protected void maxMinSumFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float maxMinSum = 0x3f3f3f;
        float sum = 0;
        int solElementsSize = sol.getElements().size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=sol.getElements().get(i);
            sum = 0;
            for(int j=0; j<solElementsSize;j++){
                nodeB=sol.getElements().get(j);
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
                sum+=instance.getDistances()[candidate.element][sol.getElements().get(j)];
            }
            if(sum < maxMinSum) maxMinSum = sum;
            setCandidateValue(candidate, maxMinSum);
        }
    }

    protected void minDiffFunction(){
        int nodeA;
        int nodeB;
        float distance;
        float max = 0;
        float min = 0x3f3f3f;
        float sum = 0;
        int solElementsSize = sol.getElements().size();
        for(int i=0; i<solElementsSize;i++){
            nodeA=sol.getElements().get(i);
            sum = 0;
            for(int j=0; j<solElementsSize;j++){
                nodeB=sol.getElements().get(j);
                distance=instance.getDistances()[nodeA][nodeB];
                sum+= distance;
            }
            if(sum > max) max = sum;
            if(sum < min) min = sum;
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
                sum+=instance.getDistances()[candidate.element][sol.getElements().get(j)];
            }
            if(sum > max) max = sum;
            if(sum < min) min = sum;
            minDiff = max-min;
            setCandidateValue(candidate, minDiff);
        }
    }

    protected void minPCenterFunction(){
        //Min P Center
        float minDist;
        int nodeA;
        int nodeB;
        float distance;
        float minPCenter = 0;
        Candidate c;

        int solElementsSize = sol.getElements().size();
        int candidatesSize = candidates.size();
        for (int k = 0; k < candidatesSize; k++) {
            c = candidates.get(k);
            candidates.remove(k);
            sol.getElements().add(c.element);

            for(int i = 0; i < candidatesSize-1; i++){
                nodeA = candidates.get(i).element;
                minDist = 0x3f3f3f;
                int auxSize = sol.getElements().size();
                for(int j = 0; j < auxSize; j++){
                    nodeB = sol.getElements().get(j);
                    distance = instance.getDistances()[nodeA][nodeB];
                    if(distance < minDist) minDist = distance;
                }
                if(minDist > minPCenter) minPCenter = minDist;
            }
            candidates.add(c);
            sol.getElements().remove(solElementsSize);
            c.value = (minPCenter);
        }
    }

}
