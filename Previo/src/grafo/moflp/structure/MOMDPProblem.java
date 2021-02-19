package grafo.moflp.structure;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MOMDPProblem implements Problem {

    private String name;
    private int m;
    private int n;
    private int p;
    private int r;
    private Candidate[] candidates;
    private Demand[] demands;
    private float[][] distance;


    public MOMDPProblem(String path) {
        super();
        load(path);
    }

    private void load(String path) {
        try {
            name = path.substring(path.lastIndexOf('/')+1);
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String[] tokens = bf.readLine().split("\\s+");
            m = Integer.parseInt(tokens[0]);
            n = Integer.parseInt(tokens[1]);
            p = Integer.parseInt(tokens[2]);
            r = Integer.parseInt(tokens[3]);
            candidates = new Candidate[m];
            for (int i = 0; i < m; i++) {
                tokens = bf.readLine().split("\\s+");
                candidates[i] = new Candidate(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[1]));
            }
            demands = new Demand[n];
            for (int i = 0; i < n; i++) {
                tokens = bf.readLine().split("\\s+");
                demands[i] = new Demand(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[1]), Float.parseFloat(tokens[2]));
            }
            evalDistances();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evalDistances() {
        distance = new float[m][n];
        for (int i = 0; i < m; i++) {
            Candidate c = candidates[i];
            for (int j = 0; j < n; j++) {
                Demand d = demands[j];
                distance[i][j] = (float) Math.sqrt(Math.pow(c.x-d.x,2)+Math.pow(c.y-d.y,2));
            }
        }
    }

    @Override
    public String getName() {
        return "MOMDP";
    }

    @Override
    public int getNumberOfVariables() {
        return p;
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
        double[] facilities = EncodingUtils.getReal(solution);
        double pmp = 0;
        double mclp = 0;
        double pcp = 0;
//        if (isFeasible(facilities) > 0) {
//            solution.setObjective(0, Integer.MAX_VALUE);
//            solution.setObjective(1, Integer.MAX_VALUE);
//            solution.setObjective(2, Integer.MAX_VALUE);
//            solution.setConstraint(0, 1);
//        } else {
        int repeated = isFeasible(facilities);
            for (int d = 0; d < n; d++) {
                Demand demand = demands[d];
                float distToClosest = distanceToClosest(facilities, d);
                // pMP
                pmp += demand.w * distToClosest;
                // MCLP
                if (distToClosest <= r) {
                    mclp += demand.w;
                }
                // pCP
                pcp = (distToClosest > pcp) ? distToClosest : pcp;
            }
            pmp /= n;
            solution.setObjective(0, pmp);
            solution.setObjective(1, -mclp);
            solution.setObjective(2, pcp);
            solution.setConstraint(0, repeated);
//        }
    }

    private int isFeasible(double[] facilites) {
        int repeated = 0;
        Set<Integer> selected = new HashSet<>();
        for (double facility : facilites) {
            int f = (int) Math.floor(facility);
            if (selected.contains(f)) {
                repeated++;
            }
            selected.add(f);
        }
        return repeated;
    }

    private float distanceToClosest(double[] facilities, int d) {
        float minDist = Float.MAX_VALUE;
        for (double fac : facilities) {
            int f = (int) Math.floor(fac);
            float dist = distance[f][d];
            if (dist < minDist) {
                minDist = dist;
            }
        }
        return minDist;
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(p, 3, 1);
        for (int i = 0; i < p; i++) {
            solution.setVariable(i, EncodingUtils.newReal(1, candidates.length));
        }
        return solution;
    }

    @Override
    public void close() {

    }
}
