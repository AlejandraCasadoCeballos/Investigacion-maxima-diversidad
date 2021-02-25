package momdp.structure;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Pareto {

    private static List<Solution> front;

    public static void reset(int numSolutions){
        front = new ArrayList<>(numSolutions);
    }

    public static boolean add(Solution solution){
        solution.calculateMetrics();
        int i = 0;
        int size = front.size();
        Solution frontSol;
        boolean enter = true;
        while(i < size){
            frontSol = front.get(i);
            /*boolean betterMaxSum = solution.getMaxSum() >= frontSol.getMaxSum();
            boolean betterMaxMin = solution.getMaxMin() >= frontSol.getMaxMin();
            boolean betterMaxMinSum = solution.getMaxMinSum() >= frontSol.getMaxMinSum();
            boolean betterMinDiff = solution.getMinDiff() <= frontSol.getMinDiff();
            boolean betterMinPCenter = solution.getMinPCenter() <= frontSol.getMinPCenter();*/
            boolean anyBetter = solution.getMaxSum() > frontSol.getMaxSum() ||
                    solution.getMaxMin() > frontSol.getMaxMin() ||
                    solution.getMaxMinSum() > frontSol.getMaxMinSum() ||
                    solution.getMinDiff() < frontSol.getMinDiff() ||
                    solution.getMinPCenter() < frontSol.getMinPCenter();

            boolean anyWorse = solution.getMaxSum() < frontSol.getMaxSum() ||
                    solution.getMaxMin() < frontSol.getMaxMin() ||
                    solution.getMaxMinSum() < frontSol.getMaxMinSum() ||
                    solution.getMinDiff() > frontSol.getMinDiff() ||
                    solution.getMinPCenter() > frontSol.getMinPCenter();


            if(anyBetter && !anyWorse){
                //domina a la solution actual
                front.remove(i);
                size--;
            } else if(!anyBetter){
                //dominada
                enter = false;
                break;
            } else i++;
        }
        if(enter)front.add(solution);
        return enter;
    }

    public static void saveToFile(String path, Instance instance){

        try(PrintWriter pw = new PrintWriter(path+"/"+instance.getName().replaceFirst(".txt","")+".txt");){
            for (Solution f: front){
                pw.println(f.getMaxSum()+" "+f.getMaxMin()+" "+f.getMaxMinSum()+" "+f.getMinDiff()+" "+f.getMinPCenter());
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    public static List<Solution> getFront() {
        return front;
    }
    public static List<Solution> getFrontCopy(){
        List<Solution> aux = new ArrayList<>(front.size());
        for(Solution s : front) aux.add(s.clone());
        return aux;
    }
}
