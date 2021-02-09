package momdp.structure;
import momdp.Main;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Instance {

    String name;
    String path;
    int numNodes;
    int numNodesSol;
    float[][] distances;
    float maxDistance = 0.0f;

    public Instance(String name, String path){
        this.path = path;
        this.name = name;
        readInstance();
    }

    public Instance(String name, int numNodes, int numNodesSol, float[][] distances){
        this.path = "";
        this.name = name;
        this.numNodes = numNodes;
        this.numNodesSol = numNodesSol;
        this.distances = distances;
    }

    private void readInstance(){
        try{
            File file = new File(path);
            Reader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            boolean firstLine = true;
            String[] parts;
            int nodeA;
            int nodeB;
            float distance;
            if(Main.DEBUG)System.out.println("\nFILE " + name);
            while((line = br.readLine()) != null){
                if(Main.DEBUG) System.out.println(line);
                parts = line.split(" ");
                if(firstLine){
                    firstLine = false;
                    numNodes = Integer.parseInt(parts[0]);
                    numNodesSol = Integer.parseInt(parts[1]);
                    distances = new float[numNodes][numNodes];
                } else {
                    nodeA = Integer.parseInt(parts[0]);
                    nodeB = Integer.parseInt(parts[1]);
                    distance = Float.parseFloat(parts[2]);
                    if(distance > maxDistance) maxDistance = distance;
                    distances[nodeA][nodeB] = distance;
                    distances[nodeB][nodeA] = distance;
                }
            }
            if(Main.DEBUG)printDistancesMatrix();

        } catch (FileNotFoundException e){
            System.out.println(("File not found " + path));
        } catch (IOException e){
            System.out.println("Error reading line on " + name);
        }
    }

    private void printDistancesMatrix(){
        System.out.println("\nDISTANCE MATRIX OF " + name);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        String zeros = "";
        int numZeros = (""+(int)maxDistance).length();
        for(int i = 0; i < numZeros; i++) zeros += '0';

        for(float[] row : distances){
            for(float value : row){
                System.out.print(new DecimalFormat(zeros+".00000", symbols).format(value) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getNumNodes() {
        return numNodes;
    }

    public int getNumNodesSol() {
        return numNodesSol;
    }

    public float[][] getDistances() {
        return distances;
    }

    public float getMaxDistance() {
        return maxDistance;
    }
}
