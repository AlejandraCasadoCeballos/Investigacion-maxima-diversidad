package momdp.algorithms;

import momdp.structure.Instance;

import java.util.ArrayList;
import java.util.List;

public class Solver {

    protected Instance instance;
    protected float[][] distances;
    protected int numNodes;
    protected int numNodesSol;
    protected List<Integer> solElements;

    public void solve(Instance instance){
        this.instance=instance;
        distances=instance.getDistances();
        numNodes=instance.getNumNodes();
        numNodesSol= instance.getNumNodesSol();
        solElements=new ArrayList<>(numNodesSol);
    }

    protected void getMetrics(){
        int maxSum=0;
        float maxMin=Float.MAX_VALUE;
        float maxMinSum=Float.MAX_VALUE;

        int nodeA;
        int nodeB;
        float distance;
        float sum;

        for(int i=0; i<numNodesSol;i++){
            nodeA=solElements.get(i);
            sum=0;
            for(int j=0; j<numNodesSol;j++){
                nodeB=solElements.get(j);
                distance=distances[nodeA][nodeB];
                if(j>i){
                    maxSum+=distance;
                    if(distance<maxMin){
                        maxMin=distance;
                    }
                }
                sum+=distance;
            }
            if(sum<maxMinSum){
                maxMinSum=sum;
            }
        }


    }



}
