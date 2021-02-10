package momdp.algorithms;

import momdp.structure.Instance;

import java.util.ArrayList;
import java.util.List;

public class Solver { //esta clase se pasa a llamar constructive, las metricas van en solution, se quitan de aqui
    //esto debería ser una interfaz, random implementa esta interfaz

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
        float minDiff;
        float minPCenter = Float.MIN_VALUE;

        int nodeA;
        int nodeB;
        float distance;
        float sum;

        float minDiffAux = Float.MIN_VALUE;

        for(int i=0; i<numNodesSol;i++){
            nodeA=solElements.get(i);
            sum=0;
            for(int j=0; j<numNodesSol;j++){
                nodeB=solElements.get(j);
                distance=distances[nodeA][nodeB];
                if(j>i){ //triangular, evitar pares repetidos
                    //maxSum
                    maxSum+=distance;

                    //maxMin
                    if(distance<maxMin){
                        maxMin=distance;
                    }
                }
                sum+=distance;
            }
            //maxMinSum
            if(sum<maxMinSum){
                maxMinSum=sum;
            }
            //minDiff
            if(sum > minDiffAux){
                minDiffAux = sum;
            }
        }
        minDiff = minDiffAux-maxMinSum;

        //Min P Center
        float minDist;
        for(int i = 0; i < numNodes; i++){
            nodeA = i;
            minDist = Float.MAX_VALUE;
            for(int j = 0; j < numNodesSol; j++){
                if(j != i){
                    nodeB = solElements.get(j);
                    distance = distances[nodeA][nodeB];
                    if(distance < minDist) minDist = distance;
                }
            }
            if(minDist > minPCenter) minPCenter = minDist;
        }

        System.out.println("MAX SUM: " + maxSum);
        System.out.println("MAX MIN: " + maxMin);
        System.out.println("MAX MIN SUM: " + maxMinSum);
        System.out.println("MIN DIFF: " + minDiff);
        System.out.println("MIN P CENTER: " + minPCenter);
    }



}
