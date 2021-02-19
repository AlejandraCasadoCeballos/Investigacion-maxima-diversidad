package grafo.moflp.structure;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MOMDPProblem implements Problem {

    private String name;
    private int n;
    private int m;
    private Integer[] candidates;
    private float[][] distances;


    public MOMDPProblem(String path) {
        super();
        load(path);
    }

    private void load(String path) {
        name = path.substring(path.lastIndexOf('/')+1);
        try(BufferedReader bf = new BufferedReader(new FileReader(path));) {

            String line;
            line = bf.readLine();
            String[] tokens = line.split("\\s+");
            n = Integer.parseInt(tokens[0]);
            m = Integer.parseInt(tokens[1]);
            distances = new float[n][n];
            candidates = new Integer[n];
            for(int i = 0; i < n; i++) candidates[i] = i;

            while((line = bf.readLine()) != null){
                tokens = line.split("\\s+");
                int nodeA = Integer.parseInt(tokens[0]);
                int nodeB = Integer.parseInt(tokens[1]);
                float dist = Float.parseFloat(tokens[2]);
                distances[nodeA][nodeB] = dist;
                distances[nodeB][nodeA] = dist;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "MOMDP";
    }

    @Override
    public int getNumberOfVariables() {
        return m;
    }

    @Override
    public int getNumberOfObjectives() {
        return 5;
    }

    @Override
    public int getNumberOfConstraints() {
        return 1;
    }

    @Override
    public void evaluate(Solution solution) {
        int[] facilities = EncodingUtils.getInt(solution);
        double maxSum = 0;
        double maxMin = 0x3f3f3f;
        double maxMinSum = 0x3f3f3f;
        double minDiff = 0;
        double minPCenter = 0;
        int repeated = isFeasible(facilities);

        int nodeA;
        int nodeB;
        float distance;
        float sum;

        float minDiffAux = 0;

        int numNodesSol = m;
        for(int i=0; i<numNodesSol;i++){
            nodeA=facilities[i];
            sum=0;
            for(int j=0; j<numNodesSol;j++){
                nodeB=facilities[j];
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
        int numNodes = n;
        for(int i = 0; i < numNodes; i++){
            nodeA = i;
            minDist = 0x3f3f3f;
            for(int j = 0; j < numNodesSol; j++){
                if(j != i){
                    nodeB = facilities[j];
                    distance = distances[nodeA][nodeB];
                    if(distance < minDist) minDist = distance;
                }
            }
            if(minDist > minPCenter) minPCenter = minDist;
        }

        solution.setObjective(0, maxSum);
        solution.setObjective(1, maxMin);
        solution.setObjective(2, maxMinSum);
        solution.setObjective(3, minDiff);
        solution.setObjective(4, minPCenter);
        solution.setConstraint(0, repeated);
    }

    private int isFeasible(int[] facilities) {
        int repeated = 0;
        Set<Integer> selected = new HashSet<>();
        for (int facility : facilities) {
            if (selected.contains(facility)) {
                repeated++;
            }
            selected.add(facility);
        }
        return repeated;
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(m, 5, 1);
        for (int i = 0; i < m; i++) {
            solution.setVariable(i, EncodingUtils.newInt(0, n-1));
        }
        return solution;
    }

    @Override
    public void close() {

    }
}
