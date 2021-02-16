import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.WFGHypervolume;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestMetrics {

    public static void prepareFronts(boolean[] minimizing, String[] paths, String pathOutRef) {
        Pareto pareto = new Pareto(minimizing);
        for (String path : paths) {
            Pareto p = new Pareto(minimizing);
            p.loadFromFile(path);
            p.saveToFile(path.substring(0, path.lastIndexOf('.'))+"_jmetal.txt");
            pareto.loadFromFile(path);
        }
        pareto.saveToFile(pathOutRef);
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Create the reference set
//        boolean[] minimizing = new boolean[]{true, false, true};
//        String pathFront1 = "medium1_nuestro.txt";
//        String pathFront2 = "medium1_moros.txt";
//        String pathReference = "pareto_reference.txt";
//        prepareFronts(minimizing, new String[]{pathFront1, pathFront2}, pathReference);

        String pathFront1 = "medium1_nuestro_jmetal.txt";
        String pathFront2 = "medium1_moros_jmetal.txt";
        String pathReference = "pareto_reference.txt";

        Front front1 = new ArrayFront(pathFront1);
        Front front2 = new ArrayFront(pathFront2);
        Front reference = new ArrayFront(pathReference);
        System.out.println("Front 1: " + front1);
        System.out.println("Front 2: " + front2);
        System.out.println("Reference: " + reference);

        SetCoverage coverage = new SetCoverage();
        double cov = coverage.evaluate(FrontUtils.convertFrontToSolutionList(front1), FrontUtils.convertFrontToSolutionList(front2));
        System.out.println("COV F1 F2: "+cov);
        double cov2 = coverage.evaluate(FrontUtils.convertFrontToSolutionList(front2), FrontUtils.convertFrontToSolutionList(front1));
        System.out.println("COV F2 F1: "+cov2);
        double cov3 = coverage.evaluate(FrontUtils.convertFrontToSolutionList(reference), FrontUtils.convertFrontToSolutionList(front1));
        System.out.println("COV RF F1: "+cov3);
        double cov4 = coverage.evaluate(FrontUtils.convertFrontToSolutionList(reference), FrontUtils.convertFrontToSolutionList(front2));
        System.out.println("COV RF F2: "+cov4);


//        Hypervolume<PointSolution> hypervolume = new PISAHypervolume<>(reference);
        Hypervolume<PointSolution> hypervolume = new WFGHypervolume<>(reference);
        System.out.println(hypervolume.isTheLowerTheIndicatorValueTheBetter());
        double hyper1 = hypervolume.evaluate(FrontUtils.convertFrontToSolutionList(front1));
        double hyper2 = hypervolume.evaluate(FrontUtils.convertFrontToSolutionList(front2));
        System.out.println("HYPER 1: "+hyper1);
        System.out.println("HYPER 2: "+hyper2);



//        FrontExtremeValues extremeValues = new FrontExtremeValues();
//        List<Double> minVals = extremeValues.findLowestValues(front1);
//        List<Double> minVals2 = extremeValues.findLowestValues(front2);
//
//        boolean normalize = true;
//        if (normalize) {
//            FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
//            referenceFront = frontNormalizer.normalize(referenceFront);
//            front = frontNormalizer.normalize(front);
//            JMetalLogger.logger.info("The fronts are NORMALIZED before computing the indicators");
//        } else {
//            JMetalLogger.logger.info("The fronts are NOT NORMALIZED before computing the indicators");
//        }
//
//        List<QualityIndicator<List<PointSolution>, Double>> indicatorList = getAvailableIndicators((Front)referenceFront);
//        if (!args[0].equals("ALL")) {
//            QualityIndicator<List<PointSolution>, Double> indicator = getIndicatorFromName(args[0], indicatorList);
//            System.out.println(indicator.evaluate(FrontUtils.convertFrontToSolutionList((Front)front)));
//        } else {
//            Iterator var8 = indicatorList.iterator();
//
//            while(var8.hasNext()) {
//                QualityIndicator<List<PointSolution>, Double> indicator = (QualityIndicator)var8.next();
//                System.out.println(indicator.getName() + ": " + indicator.evaluate(FrontUtils.convertFrontToSolutionList((Front)front)));
//            }
//
//            SetCoverage sc = new SetCoverage();
//            JMetalLogger.logger.info("SC(refPF, front): " + sc.evaluate(FrontUtils.convertFrontToSolutionList((Front)referenceFront), FrontUtils.convertFrontToSolutionList((Front)front)));
//            JMetalLogger.logger.info("SC(front, refPF): " + sc.evaluate(FrontUtils.convertFrontToSolutionList((Front)front), FrontUtils.convertFrontToSolutionList((Front)referenceFront)));
//        }
    }

    private static List<QualityIndicator<List<PointSolution>, Double>> getAvailableIndicators(Front referenceFront) throws FileNotFoundException {
        List<QualityIndicator<List<PointSolution>, Double>> list = new ArrayList();
        list.add(new Epsilon(referenceFront));
        list.add(new PISAHypervolume(referenceFront));
        list.add(new GenerationalDistance(referenceFront));
        list.add(new InvertedGenerationalDistance(referenceFront));
        list.add(new InvertedGenerationalDistancePlus(referenceFront));
        list.add(new Spread(referenceFront));
        list.add(new GeneralizedSpread(referenceFront));
        list.add(new ErrorRatio(referenceFront));
        return list;
    }

    private static QualityIndicator<List<PointSolution>, Double> getIndicatorFromName(String name, List<QualityIndicator<List<PointSolution>, Double>> list) {
        QualityIndicator<List<PointSolution>, Double> result = null;
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            QualityIndicator<List<PointSolution>, Double> indicator = (QualityIndicator)var3.next();
            if (indicator.getName().equals(name)) {
                result = indicator;
            }
        }

        if (result == null) {
            throw new JMetalException("Indicator " + name + " not available");
        } else {
            return result;
        }
    }
}
