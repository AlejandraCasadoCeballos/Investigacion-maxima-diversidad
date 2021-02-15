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

    final static String pathFolder= "./instances";
    static ArrayList<Instance> instances;

    final static int seed = 13;
    final static boolean readFromInput = false;
    final static boolean readAllFolders = false;
    final static boolean readAllInstances = false;
    final static int numSolutions = 100;
    final static String folderIndex = "GKD-a";
    final static String instanceIndex = "GKD-a_11_n10_m4.txt";

    static List<String> foldersNames;
    static List<String> instancesNames;
    static String instanceFolderPath;

    final public static boolean DEBUG = false;
    static IConstructive constructive =new RandomConstructive();

    public static void main(String[] args){

        readData();
        for (Instance instance:instances) {
            RandomManager.setSeed(seed);
            Pareto.reset(numSolutions);
            constructive.solve(instance, numSolutions);
            Pareto.saveToFile();
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
