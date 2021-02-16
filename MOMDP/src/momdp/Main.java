package momdp;

import momdp.constructive.RandomConstructive;
import momdp.constructive.IConstructive;
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
    public final static int numSolutions = 100;

    private final static boolean readFromInput = false;
    private final static boolean readAllFolders = false;
    private final static boolean readAllInstances = true;

    private final static String folderIndex = "GKD-a";
    private final static String instanceIndex = "GKD-a_11_n10_m4.txt";

    private static List<String> foldersNames;
    private static List<String> instancesNames;
    private static String instanceFolderPath;

    public final static boolean DEBUG = false;
    private static IConstructive constructive =new RandomConstructive();

    public static void main(String[] args){

        readData();
        String constructivePath=createSolFolder();
        for (Instance instance:instances) {
            RandomManager.setSeed(seed);
            Pareto.reset(numSolutions);
            constructive.solve(instance, numSolutions);
            Pareto.saveToFile(constructivePath, instance);
        }
    }

    private static void readData(){
        instances = new ArrayList<>();
        foldersNames = Arrays.asList(new File(pathFolder).list());

        if(readFromInput){
            readInstanceFromInput();
        } else {
            if(readAllFolders) readAllFolders();
            else if (foldersNames.contains(folderIndex)) readFolder(folderIndex);
            else System.out.println("Folder index exceeds the bounds of the array");
        }

    }

    public static String createSolFolder(){
        String path=pathSolFolder+"/"+constructive.getName();
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
