package momdp.structure;

import java.util.ArrayList;
import java.util.List;

public abstract class Pareto {

    static List<Solution> front;

    static boolean modifiedSinceLastAsk = false;

    public static void reset(int numSolutions){
        front = new ArrayList<>(numSolutions);
    }

    public static void add(Solution solution){
        int i = 0;
        int size = front.size();
        Solution frontSol;
        boolean enter = true;
        while(i < size){
            frontSol = front.get(i);
            boolean betterMaxSum = solution.getMaxSum() >= frontSol.getMaxSum();
            boolean betterMaxMin = solution.getMaxMin() >= frontSol.getMaxMin();
            boolean betterMaxMinSum = solution.getMaxMinSum() >= frontSol.getMaxMinSum();
            boolean betterMinDiff = solution.getMinDiff() <= frontSol.getMinDiff();
            boolean betterMinPCenter = solution.getMinPCenter() <= frontSol.getMinPCenter();

            if(!betterMaxSum && !betterMaxMin && !betterMaxMinSum && !betterMinDiff && !betterMinPCenter){
                //dominada
                enter = false;
                break;
            } else if (betterMaxSum && betterMaxMin && betterMaxMinSum && betterMinDiff && betterMinPCenter){
                //domina a la solution actual
                front.remove(i);
                size--;
            } else i++;
        }
        if(enter)front.add(solution);
    }

    public static void saveToFile(){

    }

    public static List<Solution> getFront() {
        return front;
    }

    public static boolean isModifiedSinceLastAsk() {
        return modifiedSinceLastAsk;
    }
}
