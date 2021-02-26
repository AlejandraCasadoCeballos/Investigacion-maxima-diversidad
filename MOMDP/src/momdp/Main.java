package momdp;


import momdp.constructive.*;
import momdp.constructive.grasp.*;
import momdp.localSearch.ILocalSearch;
import momdp.localSearch.LS_Swap;
import momdp.localSearch.VNS;
import momdp.structure.Instance;
import momdp.structure.Pareto;
import momdp.structure.RandomManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final static String pathFolder= "./instances";
    private final static String pathSolFolder="./pareto";
    private static ArrayList<Instance> instances;

    public final static int seed = 13;
    public final static int[] executions = new int[]{
        100,
        200,
        300
    };
    public static int numSolutions = 0;
    static float alpha=0.3f;
    static boolean randomAlpha = true;

    private final static boolean readFromInput = false;
    private final static boolean readAllFolders = false;
    private final static boolean readAllInstances = true;

    private final static String folderIndex = "preliminar";
    private final static String instanceIndex = "GKD-c_1_n500_m50.txt";

    private static List<String> instancesNames;
    private static String instanceFolderPath;

    public final static boolean DEBUG = false;
    private final static IConstructive constructive =new GRASPConstructive_Criterion1().AddLocalSearchObjs(new ILocalSearch[]{
       //new LS_Swap(),
    });
    private final static VNS vns = new VNS(new LS_Swap());
    private final static boolean useVNS = false;

    public static void main(String[] args){
        readData();
        float instanceCount = instances.size();
        int i = 0;
        //TODO: guardar en un csv el tiempo de cada instancia



        long currentTime = System.currentTimeMillis();

        for (Instance instance:instances) {
            Pareto.reset(numSolutions);
            System.out.println("Solving " + instance.getName() +", " + i/instanceCount*100f+"%");
            RandomManager.setSeed(seed);

            numSolutions = 0;
            for(int j = 0; j < executions.length; j++){
                numSolutions = executions[j]-numSolutions;
                constructive.solve(instance, numSolutions);
                if(useVNS) vns.solve(instance);
                Pareto.saveToFile(createSolFolder(), instance);
            }

            i++;
        }

        long elapsed = System.currentTimeMillis() - currentTime;
        System.out.println("Time: " + elapsed/1000f+"s");
    }

    public static float getAlpha(){
        return randomAlpha ? RandomManager.getRandom().nextFloat() : alpha;
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
        String path=pathSolFolder+"/"+constructive.getName()+(useVNS ? "_VNS_KMax_"+vns.getkMax() : "");
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
