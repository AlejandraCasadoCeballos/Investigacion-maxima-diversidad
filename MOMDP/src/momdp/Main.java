package momdp;


import momdp.constructive.*;
import momdp.constructive.grasp.*;
import momdp.localSearch.ILocalSearch;
import momdp.localSearch.LS_Swap;
import momdp.localSearch.VNS;
import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;
import momdp.structure.Solution;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final static String pathFolder= "./instances";
    private final static String pathSolFolder="./pareto";
    private final static String pathObjectivesFolder="./objectives";
    private static ArrayList<Instance> instances;

    public final static int seed = 13;
    public final static int[] executions = new int[]{100/*,200,300,400,500,600,700,800,900,1000*/};
    public final static int[] lsPCTs = new int[]{/*10,20,30,40,50,60,70,80,90,*/100};
    public static int numSolutions = 0;
    static float alpha=0.3f;
    static boolean randomAlpha = true;

    private final static boolean readFromInput = false;
    private final static boolean readAllFolders = false;
    private final static boolean readAllInstances = true;

    private final static String folderIndex = "preliminar";
    private final static String instanceIndex = "GKD-a_32_n15_m4.txt";

    private static List<String> instancesNames;
    private static String instanceFolderPath;

    public final static boolean DEBUG = false;
    private final static IConstructive constructive =new GRASPConstructive_Criterion1().AddLocalSearchObjs(new ILocalSearch[]{
       //new LS_Swap(),
    });
    private final static ILocalSearch[] localSearchForPareto = new ILocalSearch[]{
        //new LS_Swap(),
    };
    private final static VNS vns = new VNS(new LS_Swap());
    private final static boolean useVNS = false;

    public static void main(String[] args) throws IOException {
        readData();
        float instanceCount = instances.size();
        int i = 0;

        long currentTime = System.currentTimeMillis();

        long[][] times = new long[executions.length][lsPCTs.length];
        String[][] names = new String[executions.length][lsPCTs.length];

        for (Instance instance:instances){
            System.out.println("Solving " + instance.getName() +", " + i/instanceCount*100f+"%");
            RandomManager.setSeed(seed);

            for(int j = 0; j < executions.length; j++){
                numSolutions = executions[j];
                for(int w = 0; w < lsPCTs.length; w++){
                    Pareto.reset(numSolutions);
                    LS_Swap.unselectedPct = lsPCTs[w];
                    LS_Swap.selectedPct = lsPCTs[w];
                    long instanceTime = System.currentTimeMillis();
                    constructive.solve(instance, numSolutions);
                    for(ILocalSearch ls : localSearchForPareto){
                        List<Solution> solutions = Pareto.getFrontCopy();
                        for(Solution s : solutions){
                            ls.localSearchSolution(s);
                        }
                    }
                    if(useVNS) vns.solve(instance);
                    times[j][w] += (System.currentTimeMillis() - instanceTime);
                    String path = createSolFolder();
                    names[j][w] = path.substring(pathSolFolder.length() +1);
                    Pareto.saveToFile(path, createObjectiveFolder(), instance);
                }
            }
            i++;
        }

        /*for (Instance instance:instances) {
            Pareto.reset(numSolutions);
            System.out.println("Solving " + instance.getName() +", " + i/instanceCount*100f+"%");
            RandomManager.setSeed(seed);

            numSolutions = 0;
            int count = 0;
            for(int j = 0; j < executions.length; j++){
                long instanceTime = System.currentTimeMillis();
                numSolutions = executions[j];
                constructive.solve(instance, executions[j]-count);
                for(ILocalSearch ls : localSearchForPareto){
                    List<Solution> solutions = Pareto.getFrontCopy();
                    for(Solution s : solutions){
                        ls.localSearchSolution(s);
                    }
                }
                count = executions[j];
                if(useVNS) vns.solve(instance);
                times[j] += (System.currentTimeMillis() - instanceTime);
                Pareto.saveToFile(createSolFolder(), instance);
            }

            i++;
        }*/

        long elapsed = System.currentTimeMillis() - currentTime;
        System.out.println("Total time: " + elapsed/1000f+"s");
        saveTimes(times, names);
        Pareto.saveTotalObjectives(pathObjectivesFolder);
    }

    public static float getAlpha(){
        return randomAlpha ? RandomManager.getRandom().nextFloat() : alpha;
    }

    private static void saveTimes(long[][] times, String[][] names) throws IOException {
        File file =new File(pathSolFolder+"/times.csv");
        if(!file.exists()) file.createNewFile();

        List<String> allLines = new ArrayList<>();
        boolean[][] overrideCheck = new boolean[names.length][names[0].length];

        try(BufferedReader br = new BufferedReader(new FileReader(file.getPath()))){
            String line;
            String[] parts;

            while((line = br.readLine()) != null){
                parts = line.split(";");
                boolean override = false;
                for(int j = 0; j < names.length; j++){
                    for(int w = 0; w < names[0].length; w++){
                        if(parts[0].equals(names[j][w])){
                            override = true;
                            allLines.add(names[j][w]+";"+(times[j][w]/1000.0));
                            overrideCheck[j][w] = true;
                            break;
                        }
                    }
                    if(override) break;
                }
                if(!override) allLines.add(line);
            }
            for(int j = 0; j < names.length; j++){
                for(int w = 0; w < names[0].length; w++){
                    if(!overrideCheck[j][w]){
                        allLines.add(names[j][w]+";"+(times[j][w]/1000.0));
                    }
                }
            }
        } catch (FileNotFoundException e){
            System.out.println(("File not found " + file.getPath()));
        } catch (IOException e){
            System.out.println("Error reading line on " + file.getName());
        }
        System.out.println("Times: ");
        try(PrintWriter pw = new PrintWriter(file.getPath())){
            for(String l : allLines){
                System.out.println(l);
                pw.println(l);
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    private static void readData(){
        instances = new ArrayList<>();
        List<String> foldersNames = Arrays.asList(new File(pathFolder).list());

        if(readFromInput){
            readInstanceFromInput();
        } else {
            if(readAllFolders) readAllFolders();
            else if (foldersNames.contains(folderIndex)) readFolder(folderIndex);
            else System.out.println("Folder index exceeds the bounds of the array");
        }

    }

    public static String createSolFolder(){
        String path=pathSolFolder+"/"+constructive.getName()+(useVNS ? "_VNS_KMax_"+vns.getkMax()+"_KStep_"+vns.getkStep() : "");
        if(localSearchForPareto.length > 0){
            path += "_LSforPareto";
        }
        for(ILocalSearch ls : localSearchForPareto){
            path+= "_" + ls.getName();
        }
        File file =new File(path);
        if(!file.exists()){
            boolean bool = file.mkdir();
            if(!bool) System.out.println("Problem creating the folder: "+ constructive.getName());
        }

        return path;
    }

    public static String createObjectiveFolder(){
        String path=pathObjectivesFolder+"/"+constructive.getName()+(useVNS ? "_VNS_KMax_"+vns.getkMax()+"_KStep_"+vns.getkStep() : "");
        if(localSearchForPareto.length > 0){
            path += "_LSforPareto";
        }
        for(ILocalSearch ls : localSearchForPareto){
            path+= "_" + ls.getName();
        }
        path+="_objectives";
        File file =new File(path);
        if(!file.exists()){
            boolean bool = file.mkdir();
            if(!bool) System.out.println("Problem creating the folder: "+ constructive.getName());
        }

        return path;
    }

    private static void readInstanceFromInput(){
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] parts = line.split(" ");
        int numNodes = Integer.parseInt(parts[0]);
        int numNodesSol = Integer.parseInt(parts[1]);
        int a;
        int b;
        float d;
        float[][] distances = new float[numNodes][numNodes];
        for(int i = 0; i < numNodes;i++){
            for(int j = i+1; j < numNodes; j++){
                parts = sc.nextLine().split(" ");
                a = Integer.parseInt(parts[0]);
                b = Integer.parseInt(parts[1]);
                d = Float.parseFloat(parts[2]);
                distances[a][b] = d;
                distances[b][a] = d;
            }
        }
        instances.add(new Instance("input instance", numNodes, numNodesSol, distances));
    }

    private static void readAllFolders(){
        instances = new ArrayList<>();
        String [] folders =new File(pathFolder).list();

        for(String fileName : folders){
            readFolder(fileName);
        }
    }

    private static void readFolder(String fileName){
        File file;
        file=new File(pathFolder+"/"+fileName);
        if(!fileName.startsWith(".") && !fileName.startsWith("..") && file.isDirectory()){
            instancesNames = Arrays.asList(file.list());
            instanceFolderPath = file.getPath() + "/";
            if(readAllInstances) readAllInstances();
            else if (instancesNames.contains(instanceIndex)) readInstance(instanceIndex);
            else System.out.println("Instance index exceeds the bounds of the array");
        }
    }

    private static void readAllInstances(){
        for(String instanceName : instancesNames){
            readInstance(instanceName);
        }
    }

    private static void readInstance(String instanceName){
        instances.add(new Instance(instanceName,instanceFolderPath +instanceName));
    }
}
