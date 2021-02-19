package grafo.moflp.solve;

import grafo.moflp.structure.MOFLPProblem;
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

        String pathIn = "/Users/jesus.sanchezoro/ResultadosInvestigacion/moflp/best_previo/nuevas/";
        String instanceSet = (args.length>0)?args[0]:"instancias";
        String dir = ((args.length>0)?args[1]:pathIn)+instanceSet;
        String outDir = "./pareto/";
        File outDirCreator = new File(outDir);
        outDirCreator.mkdirs();

        String[] fileNames = new File(dir).list((dir1, name) -> name.endsWith(".txt"));
        PrintWriter pw = new PrintWriter("mflp_moea.csv");
        for (String fileName : fileNames) {
            System.out.print(fileName+"\t");
            pw.print(fileName.replace(".txt","")+";");
            String path = dir+"/"+fileName;
            BufferedReader bf = new BufferedReader(new FileReader(path));
            int p = Integer.parseInt(bf.readLine().split("\\s+")[2]);
            bf.close();
            long timeIni = System.currentTimeMillis();
            NondominatedPopulation result = new Executor()
                    .withProblemClass(MOFLPProblem.class, path)
                    .withAlgorithm("AbYSS")
                    .withMaxEvaluations(6000000)
                    .withProperty("populationSize", 10000)
                    .run();
            double secs = (System.currentTimeMillis()-timeIni)/1000.0;

            PrintWriter pwPareto = new PrintWriter(outDir+"/"+fileName);
            pwPareto.println("PMP;MCLP;PCP");
            for (int i = 0; i < result.size(); i++) {
                Solution sol = result.get(i);
                double[] obj = sol.getObjectives();
                System.out.print("Solution "+(i+1)+": "+obj[0]+", "+(-obj[1])+", "+obj[2]+" -> ");
                pwPareto.println(obj[0]+";"+(-obj[1])+";"+obj[2]);
                for (int j = 0; j < p; j++) {
                    int fac = (int)Math.floor(EncodingUtils.getReal(sol.getVariable(j)));
                    System.out.print(fac  + " ");
                }
                System.out.println();
            }
            pw.println(secs);
            System.out.println(result.size()+"\t"+secs);
            pwPareto.close();
        }
        pw.close();
        //String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/moflp/toy.txt";
//        String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/moflp/instancias/large1.txt";
//        BufferedReader bf = new BufferedReader(new FileReader(path));
//        int p = Integer.parseInt(bf.readLine().split("\\s+")[2]);
//        bf.close();
//        long timeIni = System.currentTimeMillis();
//        NondominatedPopulation result = new Executor()
//                .withProblemClass(MOFLPProblem.class, path)
//                .withAlgorithm("AbYSS")
//                .withMaxEvaluations(1000000)
//                .withProperty("populationSize", 1000)
//                .run();
//        double secs = (System.currentTimeMillis()-timeIni)/1000.0;
//
//        for (int i = 0; i < result.size(); i++) {
//            Solution sol = result.get(i);
//            double[] obj = sol.getObjectives();
//            System.out.print("Solution "+(i+1)+": "+obj[0]+", "+obj[1]+", "+obj[2]+" -> ");
//            for (int j = 0; j < p; j++) {
//                int fac = (int)Math.floor(EncodingUtils.getReal(sol.getVariable(j)));
//                if (j == 1) {
//                    fac = -fac; // Minimization
//                }
//                System.out.print(fac  + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("Total time: "+secs);
    }
}
