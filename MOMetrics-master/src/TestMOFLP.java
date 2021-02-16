import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.SetCoverage;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TestMOFLP {

    public static void prepareFronts(boolean[] minimizing, String[] paths, String[] pathsOut, String instanceName, String pathOutRef) {
        Pareto pareto = new Pareto(minimizing);
        for (int i = 0; i < paths.length; i++) {
            Pareto p = new Pareto(minimizing);
            p.loadFromFile(paths[i]+"/"+instanceName);
            p.saveToFile(pathsOut[i]+"/"+instanceName);
            pareto.loadFromFile(paths[i]+"/"+instanceName);
        }
        pareto.saveToFile(pathOutRef);
    }

    public static void evaluateSingleInstance() throws FileNotFoundException {
        boolean[] minimizing = new boolean[]{true, false, true};
        String instanceName = "small1.txt";
        String pathSS = "moflp/ParallelScatterSearchV2(50,10)";
        String pathPrev = "moflp/best_previo";
        String pathOutRef = "moflp/refOut"+instanceName.replace(".txt","")+"_reference.txt";
        /*
        Creates the reference set and multiplies by -1 those objectives that are minimizing
         */
//        prepareFronts(minimizing, new String[]{pathSS+"/"+instanceName, pathPrev+"/"+instanceName}, pathOutRef);
        String jmetalFrontSSPath = (pathSS+"/"+instanceName).replace(".txt", "_jmetal.txt");
        String jmetalFrontPrevioPath = (pathPrev+"/"+instanceName).replace(".txt", "_jmetal.txt");
        Front frontSS = new ArrayFront(jmetalFrontSSPath);
        Front frontPrevio = new ArrayFront(jmetalFrontPrevioPath);
        Front frontRef = new ArrayFront(pathOutRef);
        System.out.println(frontSS);
        System.out.println(frontPrevio);
        System.out.println(frontRef);

        /*
        NORMALIZATION
         */
        FrontNormalizer frontNormalizer = new FrontNormalizer(frontRef);
        Front frontSSNorm = frontNormalizer.normalize(frontSS);
        Front frontPrevioNorm = frontNormalizer.normalize(frontPrevio);
        Front frontRefNorm = frontNormalizer.normalize(frontRef);
        System.out.println(frontSSNorm);
        System.out.println(frontPrevioNorm);
        System.out.println(frontRefNorm);
        List<PointSolution> solsSS = FrontUtils.convertFrontToSolutionList(frontSSNorm);
        List<PointSolution> solsPrevio = FrontUtils.convertFrontToSolutionList(frontPrevioNorm);
        List<PointSolution> solsRef = FrontUtils.convertFrontToSolutionList(frontRefNorm);

        /*
        COVERAGE
         */
        System.out.println("==========");
        System.out.println("\tCOVERAGE");
        System.out.println("==========");
        SetCoverage coverage = new SetCoverage();
        double cov = coverage.evaluate(solsSS, solsPrevio);
        System.out.println("COV SS PREVIO: "+cov);
        double cov2 = coverage.evaluate(solsPrevio, solsSS);
        System.out.println("COV PREVIO SS: "+cov2);
        double cov3 = coverage.evaluate(solsRef, solsSS);
        System.out.println("COV RF SS: "+cov3);
        double cov4 = coverage.evaluate(solsRef, solsPrevio);
        System.out.println("COV RF PREVIO: "+cov4);

        /*
        HYPERVOLUME
         */
        System.out.println("===========");
        System.out.println("\tHYPERVOLUME");
        System.out.println("===========");
        Hypervolume<PointSolution> hypervolume = new PISAHypervolume<>(frontRefNorm);
        double hvSS = hypervolume.evaluate(solsSS);
        double hvPrevio = hypervolume.evaluate(solsPrevio);
        System.out.println("HV SS: "+hvSS);
        System.out.println("HV PREV: "+hvPrevio);

        /*
        EPSILON
         */
        System.out.println("==========");
        System.out.println("\tEPSILON");
        System.out.println("==========");
        Epsilon<PointSolution> epsilon = new Epsilon<>(frontRefNorm);
        double epsSS = epsilon.evaluate(solsSS);
        double epsPrevio = epsilon.evaluate(solsPrevio);
        System.out.println("EPS SS: " + epsSS);
        System.out.println("EPS PREV: " + epsPrevio);
    }

    public static void evaluateDir() throws FileNotFoundException {
        boolean[] minimizing = new boolean[]{true, false, true};
        String pathSSOriginal = "moflp/ParallelScatterSearchV3(GRASP(0, -1.00),GRASP(1, -1.00),GRASP(2, 0.25),50,5,false)";
        String pathSS = "moflp/JMetal/ParallelScatterSearchV3(GRASP(0, -1.00),GRASP(1, -1.00),GRASP(2, 0.25),50,5,false)";
        String pathPrevOriginal = "moflp/best_previo";
        String pathPrev = "moflp/JMetal/best_previo";
        String pathOutRef = "moflp/JMetal/reference";
        new File(pathOutRef).mkdirs();
//        new File("moflp/JMetal").mkdirs();

        try (PrintWriter pw = new PrintWriter("moflp_analysis.csv")) {
            pw.println("INSTANCE;CV 1-2;CV 2-1;CV R-1;CV R-2;HV 1;HV 2;EPS 1;EPS 2");
            String[] files = new File(pathSSOriginal).list((dir, name) -> name.endsWith(".txt"));
            for (String instanceName : files) {
                try {
                    System.out.println(instanceName);
                    pw.print(instanceName+";");
                    String pathOutRefSet = pathOutRef + "/" + instanceName.replace(".txt", "") + "_reference.txt";
                    /*
                    Creates the reference set and multiplies by -1 those objectives that are minimizing
                     */
                    prepareFronts(minimizing, new String[]{pathSSOriginal, pathPrevOriginal},
                            new String[]{pathSS, pathPrev}, instanceName, pathOutRefSet);
                    Front frontSS = new ArrayFront(pathSS+"/"+instanceName);
                    Front frontPrevio = new ArrayFront(pathPrev+"/"+instanceName);
                    Front frontRef = new ArrayFront(pathOutRefSet);

                    /*
                    NORMALIZATION
                     */
                    FrontNormalizer frontNormalizer = new FrontNormalizer(frontRef);
                    Front frontSSNorm = frontNormalizer.normalize(frontSS);
                    Front frontPrevioNorm = frontNormalizer.normalize(frontPrevio);
                    Front frontRefNorm = frontNormalizer.normalize(frontRef);
                    List<PointSolution> solsSS = FrontUtils.convertFrontToSolutionList(frontSSNorm);
                    List<PointSolution> solsPrevio = FrontUtils.convertFrontToSolutionList(frontPrevioNorm);
                    List<PointSolution> solsRef = FrontUtils.convertFrontToSolutionList(frontRefNorm);

                    /*
                    COVERAGE
                     */
                    SetCoverage coverage = new SetCoverage();
                    double cov = coverage.evaluate(solsSS, solsPrevio);
                    pw.print(cov + ";");
                    double cov2 = coverage.evaluate(solsPrevio, solsSS);
                    pw.print(cov2 + ";");
                    double cov3 = coverage.evaluate(solsRef, solsSS);
                    pw.print(cov3 + ";");
                    double cov4 = coverage.evaluate(solsRef, solsPrevio);
                    pw.print(cov4 + ";");

                    /*
                    HYPERVOLUME
                     */
                    Hypervolume<PointSolution> hypervolume = new PISAHypervolume<>(frontRefNorm);
                    double hvSS = hypervolume.evaluate(solsSS);
                    double hvPrevio = hypervolume.evaluate(solsPrevio);
                    pw.print(hvSS + ";" + hvPrevio + ";");

                    /*
                    EPSILON
                     */
                    Epsilon<PointSolution> epsilon = new Epsilon<>(frontRefNorm);
                    double epsSS = epsilon.evaluate(solsSS);
                    double epsPrevio = epsilon.evaluate(solsPrevio);
                    pw.print(epsSS + ";" + epsPrevio);
                } catch (JMetalException e) {
                    e.printStackTrace();
                    System.err.println("ERROR EN INSTANCIA: "+instanceName);
                }
                pw.println();
            }
        } catch (IOException e ){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        evaluateDir();
    }
}
