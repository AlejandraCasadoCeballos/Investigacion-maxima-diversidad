package grafo.moflp.solve;

import grafo.moflp.structure.MOMDPProblem;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

import java.io.*;
import java.util.Calendar;

public class MOFLPSolver {

    public static void main(String[] args) throws IOException {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);

        String pathIn = "../MOMDP/instances/";
        String instanceSet = (args.length>0)?args[0]:"preliminar";
        String dir = ((args.length>0)?args[1]:pathIn)+instanceSet;
        String outDir = "./pareto/";
        File outDirCreator = new File(outDir);
        outDirCreator.mkdirs();

        String[] fileNames = new File(dir).list((dir1, name) -> name.endsWith(".txt"));
        PrintWriter pw = new PrintWriter("momdp_moea.csv");
        double totalTime=0;
        for (String fileName : fileNames) {
            System.out.print(fileName+"\t");
            pw.print(fileName.replace(".txt","")+";");
            String path = dir+"/"+fileName;
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String[] parts= bf.readLine().split("\\s+");
            int p = Integer.parseInt(parts[1]);
            bf.close();
            long timeIni = System.currentTimeMillis();
            NondominatedPopulation result = new Executor()
                    .withProblemClass(MOMDPProblem.class, path)
                    .withAlgorithm("NSGAIII")
                    .withMaxEvaluations(250000) //250000
                    .withProperty("populationSize", 500)
                    .run();
            double millisecs = (System.currentTimeMillis()-timeIni);
            totalTime+=millisecs;
            PrintWriter pwPareto = new PrintWriter(outDir+"/"+fileName);
            for (int i = 0; i < result.size(); i++) {
                Solution sol = result.get(i);
                double[] obj = sol.getObjectives();
                System.out.print("Solution "+(i+1)+": "+(-obj[0])+" "+(-obj[1])+" "+(-obj[2])+" "+obj[3]+" "+obj[4]+" ");
                pwPareto.println((-obj[0])+" "+(-obj[1])+" "+(-obj[2])+" "+obj[3]+" "+obj[4]);
                System.out.println();
            }
            pw.println(millisecs);
            System.out.println(result.size()+"\t"+millisecs);
            pwPareto.close();
        }
        System.out.println(totalTime);
        pw.close();
    }
}
