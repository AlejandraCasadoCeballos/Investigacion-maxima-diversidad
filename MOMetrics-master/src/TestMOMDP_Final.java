import org.uma.jmetal.qualityindicator.impl.*;
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

public class TestMOMDP_Final {

    public static void prepareFronts(boolean[] minimizing, String pathGlobal, String[] paths, String pathJmetal, String[] pathsOut, String instanceName, String pathOutRef) {
        Pareto pareto = new Pareto(minimizing);
        for (int i = 0; i < paths.length; i++) {
            Pareto p = new Pareto(minimizing);
            p.loadFromFile(pathGlobal+"/"+paths[i]+"/"+instanceName);
            p.saveToFile(pathJmetal+"/"+pathsOut[i]+"/"+instanceName);
            pareto.loadFromFile(pathGlobal+"/"+paths[i]+"/"+instanceName);
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

        /*
        IGD+
         */
        System.out.println("==========");
        System.out.println("\tIGD+");
        System.out.println("==========");
        InvertedGenerationalDistancePlus<PointSolution> igd = new InvertedGenerationalDistancePlus<>(frontRefNorm);
        double igdSS = igd.evaluate(solsSS);
        double igdPrevio = igd.evaluate(solsPrevio);
        System.out.println("EPS SS: " + epsSS);
        System.out.println("EPS PREV: " + epsPrevio);
    }

    public static void evaluateDir() throws FileNotFoundException {
        String pathGlobal = "../MOMDP/pareto";
        String[] paths = new String[]{
                "GRASPConstructive_Criterion1_Seed_13_SolCount_250",
                "previo",
        };
        boolean[] minimizing = new boolean[]{false, false, false, true, true};
        String pathJMetal = pathGlobal+"/jmetal";
        new File(pathJMetal).mkdirs();

        try (PrintWriter pw = new PrintWriter("momdp_analysis.csv")) {
            String matching = "";
            String headers = "INSTANCE";
            for (int i = 0; i < paths.length; i++) {
                matching += paths[i]+";"+(i+1)+"\n";
                headers += ";CV REF-"+(i+1)+";HV "+(i+1)+";EPS "+(i+1)+";GD "+(i+1)+";SIZE "+(i+1)+";SPREAD "+(i+1)+";IGD "+(i+1);
            }
            pw.println(matching);
            pw.println(headers);
            String[] files = new File(pathGlobal+"/"+paths[0]).list((dir, name) -> name.endsWith(".txt"));
            for (String instanceName : files) {
                try {
                    System.out.println(instanceName);
                    pw.print(instanceName+";");
                    String pathOutRefSet = pathJMetal + "/" + instanceName.replace(".txt", "") + "_reference.txt";
                    /*
                    Creates the reference set and multiplies by -1 those objectives that are minimizing
                     */
                    prepareFronts(minimizing, pathGlobal, paths, pathJMetal, paths, instanceName, pathOutRefSet);
                    Front frontRef = new ArrayFront(pathOutRefSet);
                    Front[] fronts = new Front[paths.length];
                    for (int i = 0; i < paths.length; i++) {
                        fronts[i] = new ArrayFront(pathJMetal+"/"+paths[i]+"/"+instanceName);
                    }

                    /*
                    NORMALIZATION
                     */
                    FrontNormalizer frontNormalizer = new FrontNormalizer(frontRef);
                    Front normRef = frontNormalizer.normalize(frontRef);
                    List<PointSolution> solsRef = FrontUtils.convertFrontToSolutionList(normRef);
                    Front[] normFronts = new Front[fronts.length];
                    List<PointSolution>[] sols = new List[fronts.length];
                    for (int i = 0; i < normFronts.length; i++) {
                        normFronts[i] = frontNormalizer.normalize(fronts[i]);
                        sols[i] = FrontUtils.convertFrontToSolutionList(normFronts[i]);
                    }

                    SetCoverage coverage = new SetCoverage();
                    Hypervolume<PointSolution> hypervolume = new PISAHypervolume<>(normRef);
                    Epsilon<PointSolution> epsilon = new Epsilon<>(normRef);
                    GenerationalDistance<PointSolution> gd = new GenerationalDistance<>();
                    GeneralizedSpread<PointSolution> sp = new GeneralizedSpread<>();
                    InvertedGenerationalDistancePlus<PointSolution> igd = new InvertedGenerationalDistancePlus<>();
                    //System.out.println(igd.isTheLowerTheIndicatorValueTheBetter());
                    for (int i = 0; i < sols.length; i++) {
                        double cov = coverage.evaluate(solsRef, sols[i]);
                        double hv = hypervolume.evaluate(sols[i]);
                        double eps = epsilon.evaluate(sols[i]);
                        double gdVal = gd.generationalDistance(fronts[i], normRef);
                        double spVal = sp.generalizedSpread(fronts[i], normRef);
                        double igdVal = igd.invertedGenerationalDistancePlus(fronts[i], normRef);
                        pw.print(cov+";"+hv+";"+eps+";"+gdVal+";"+sols[i].size()+";"+spVal+";"+igdVal+";");
                    }
                } catch (JMetalException e) {
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
