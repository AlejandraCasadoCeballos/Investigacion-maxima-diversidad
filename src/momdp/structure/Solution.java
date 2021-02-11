package momdp.structure;

import java.util.List;

public class Solution {

    Instance instance;
    protected List<Integer> solElements;

    public Solution(Instance instance, List<Integer> solElements){
        this.instance = instance;
        this.solElements = solElements;
        print();
        calculateMetrics();
    }

    private void print(){
        System.out.println("Solution elements");
        for(int i : solElements){
            System.out.print(i + " ");
        }
        System.out.println();
    }


    protected void calculateMetrics(){
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

        for(int i=0; i<instance.getNumNodesSol();i++){
            nodeA=solElements.get(i);
            sum=0;
            for(int j=0; j<instance.getNumNodesSol();j++){
                nodeB=solElements.get(j);
                distance=instance.getDistances()[nodeA][nodeB];
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
        for(int i = 0; i < instance.getNumNodes(); i++){
            nodeA = i;
            minDist = Float.MAX_VALUE;
            for(int j = 0; j < instance.getNumNodesSol(); j++){
                if(j != i){
                    nodeB = solElements.get(j);
                    distance = instance.getDistances()[nodeA][nodeB];
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
