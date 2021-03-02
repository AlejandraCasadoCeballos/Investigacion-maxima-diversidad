package momdp.structure;


import momdp.constructive.grasp.GRASPConstructive;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Pareto {

    private static List<Solution> front;
    private static List<Solution> discarded;

    private static int[] totalCounts = new int[5];
    private static int[][] defeatedTotalCount = new int[5][5];

    public static void reset(int numSolutions){
        front = new ArrayList<>(numSolutions);
        discarded = new ArrayList<>(3000);
    }

    public static boolean add(Solution solution){
        solution.calculateMetrics();
        int i = 0;
        int size = front.size();
        Solution frontSol;
        boolean enter = true;
        while(i < size){
            frontSol = front.get(i);
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
                frontSol.setDefeatedBy(solution.getObjective());
                front.remove(i);
                discarded.add(frontSol);
                size--;
            } else if(!anyBetter){
                //dominada
                enter = false;
                break;
            } else i++;
        }
        if(enter)front.add(solution.clone());
        return enter;
    }

    public static void saveTotalObjectives(String path){
        path += "/lastObjectives.csv";
        //File file = new File(path);
        /*try{
            if(!file.exists()) file.createNewFile();
        } catch(IOException e){

        }*/

        try(PrintWriter pw = new PrintWriter(path)){

            pw.println("Objective counts for pareto solutions:");
            for(int i = 0; i < totalCounts.length; i++){
                pw.println(i+";"+totalCounts[i]);
            }

            pw.println("\nObjective functions times:");
            pw.println("maxSum;"+ GRASPConstructive.maxSumTime);
            pw.println("maxMin;"+ GRASPConstructive.maxMinTime);
            pw.println("maxMinSum;"+ GRASPConstructive.maxMinSumTime);
            pw.println("minDiff;"+ GRASPConstructive.minDiffTime);
            pw.println("minPCenter;"+ GRASPConstructive.minPCenterTime);

            /*pw.println();
            pw.println("Y: objective, X: times the X objective has been defeated by Y objective");

            for(int y = 0; y < 6; y++){
                if(y == 0) pw.println("X/Y;1;2;3;4;5");
                else {
                    pw.print(y+";");
                    for(int x = 0; x < 5; x++){
                        pw.print(defeatedTotalCount[x][y-1]);
                        if(x == 4) pw.print("\n");
                        else pw.print(";");
                    }
                }
            }*/
        }
        catch (IOException e){
            System.out.println(e);
        }

    }

    public static void saveToFile(String solutionPath, String objectivesPath, Instance instance){
        try(PrintWriter pw = new PrintWriter(solutionPath+"/"+instance.getName().replaceFirst(".txt","")+".txt");){
            for (Solution f: front){
                pw.println(f.getMaxSum()+" "+f.getMaxMin()+" "+f.getMaxMinSum()+" "+f.getMinDiff()+" "+f.getMinPCenter());
            }
        }
        catch (IOException e){
            System.out.println(e);
        }

        try(PrintWriter pw = new PrintWriter(objectivesPath+"/"+instance.getName().replaceFirst(".txt","")+"_objectives_"+".txt");){
            int[] counts = new int[5];
            for (Solution f: front){
                if(f.getObjective() >= 0) {
                    counts[f.getObjective()]++;
                    totalCounts[f.getObjective()]++;
                }
                //pw.println(f.getMaxSum()+" "+f.getMaxMin()+" "+f.getMaxMinSum()+" "+f.getMinDiff()+" "+f.getMinPCenter());
            }
            pw.println("Objective counts for pareto solutions:");
            for(int i = 0; i < counts.length; i++){
                pw.println(i+";"+counts[i]);
            }

            pw.println();
            pw.println("Y: objective, X: times the X objective has been defeated by Y objective");
            int[][] defeatedCount = new int[5][5];
            for(Solution d : discarded){
                if(d.getDefeatedBy() >= 0 && d.getObjective() >= 0){
                    defeatedCount[d.getObjective()][d.getDefeatedBy()]++;
                    defeatedTotalCount[d.getObjective()][d.getDefeatedBy()]++;
                }
            }
            for(int y = 0; y < 6; y++){
                if(y == 0) pw.println("X/Y;1;2;3;4;5");
                else {
                    pw.print(y+";");
                    for(int x = 0; x < 5; x++){
                        pw.print(defeatedCount[x][y-1]);
                        if(x == 4) pw.print("\n");
                        else pw.print(";");
                    }
                }
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
