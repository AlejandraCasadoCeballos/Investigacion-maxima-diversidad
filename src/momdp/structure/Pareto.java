package momdp.structure;

import momdp.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Pareto {

    private static List<Solution> front;

    private static boolean modifiedSinceLastAsk = false;

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

    public static void saveToFile(String path, Instance instance){
        try(FileWriter fw=new FileWriter(path+"/"+instance.getName().replaceFirst(".txt","")+"_"+ Main.numSolutions+".txt");
            BufferedWriter bw=new BufferedWriter(fw)){

            for (Solution f: front){
                bw.write(f.getMaxSum()+" "+f.getMaxMin()+" "+f.getMaxMinSum()+" "+f.getMinDiff()+" "+f.getMinPCenter());
                bw.newLine();
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    public static List<Solution> getFront() {
        return front;
    }

    public static boolean isModifiedSinceLastAsk() {
        return modifiedSinceLastAsk;
    }
}
