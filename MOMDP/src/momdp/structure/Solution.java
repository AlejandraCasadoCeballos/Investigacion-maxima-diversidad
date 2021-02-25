package momdp.structure;

import momdp.Main;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    private Instance instance;
    protected List<Integer> solElements;
    private float maxSum;
    private float maxMin;
    private float maxMinSum;
    private float minDiff;
    private float minPCenter;

    public Solution(Instance instance/*, List<Integer> solElements*/){
        this.instance = instance;
        this.solElements = new ArrayList<Integer>(instance.getNumNodesSol());
        if(Main.DEBUG) print();

    }

    public Solution clone(){
        Solution sol = new Solution(instance);
        for(Integer i : solElements)
            sol.getElements().add(i);
        return sol;
    }

    public List<Integer> getElements(){
        return solElements;
    }

    private void print(){
        System.out.println("Solution elements");
        for(int i : solElements){
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public void calculateMetrics(){
        maxSum=0;
        maxMin=0x3f3f3f;
        maxMinSum=0x3f3f3f;
        minPCenter = 0;

        int nodeA;
        int nodeB;
        float distance;
        float sum;

        float minDiffAux = 0;

        int numNodesSol = instance.getNumNodesSol();
        for(int i=0; i<numNodesSol;i++){
            nodeA=solElements.get(i);
            sum=0;
            for(int j=0; j<numNodesSol;j++){
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
        int numNodes = instance.getNumNodes();
        for(int i = 0; i < numNodes; i++){
            nodeA = i;
            minDist = 0x3f3f3f;
            for(int j = 0; j < numNodesSol; j++){
                if(j != i){
                    nodeB = solElements.get(j);
                    distance = instance.getDistances()[nodeA][nodeB];
                    if(distance < minDist) minDist = distance;
                }
            }
            if(minDist > minPCenter) minPCenter = minDist;
        }

        if(Main.DEBUG){
            System.out.println("MAX SUM: " + maxSum);
            System.out.println("MAX MIN: " + maxMin);
            System.out.println("MAX MIN SUM: " + maxMinSum);
            System.out.println("MIN DIFF: " + minDiff);
            System.out.println("MIN P CENTER: " + minPCenter);
        }
    }

    public float getMaxSum() {
        return maxSum;
    }

    public float getMaxMin() {
        return maxMin;
    }

    public float getMaxMinSum() {
        return maxMinSum;
    }

    public float getMinDiff() {
        return minDiff;
    }

    public float getMinPCenter() {
        return minPCenter;
    }

    public Instance getInstance() {
        return instance;
    }

    public List<Integer> getSolElements() {
        return solElements;
    }
}
