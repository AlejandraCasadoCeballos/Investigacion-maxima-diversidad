package momdp;

import momdp.algorithms.RandomSolver;
import momdp.algorithms.Solver;
import momdp.structure.Instance;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    final static String pathFolder= "./instances";
    static ArrayList<Instance> instances;

    final static boolean readAllFolders = false;
    final static boolean readAllInstances = false;
    final static String folderIndex = "GKD-a";
    final static String instanceIndex = "GKD-a_1_n10_m2.txt";

    static List<String> foldersNames;
    static List<String> instancesNames;
    static String instanceFolderPath;

    final public static boolean DEBUG = true;
    static Solver solver=new RandomSolver();

    public static void main(String[] args){
        readData();
        for (Instance instance:instances) {
            solver.solve(instance);
        }

    }

    private static void readData(){
        instances = new ArrayList<>();
        foldersNames = Arrays.asList(new File(pathFolder).list());


        if(readAllFolders) readAllFolders();
        else if (foldersNames.contains(folderIndex)) readFolder(folderIndex);
        else System.out.println("Folder index exceeds the bounds of the array");
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
