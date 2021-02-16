import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Pareto {

    private List<double[]> front;
    private boolean[] minimizing;

    public Pareto(boolean[] minimizing) {
        front = new ArrayList<>(1000);
        this.minimizing = minimizing;
    }

    public int compareDouble(double f1, double f2) {
        if (Math.abs(f1-f2) < 0.0001) {
            return 0;
        } else if (f1 < f2) {
            return -1;
        } else {
            return +1;
        }
    }

    public boolean add(double[] solution) {
        List<Integer> dominated = new ArrayList<>();
        boolean enter = true;
        int idx = 0;
        for (double[] frontSol : front) {
            boolean bestInAll = true;
            boolean worstInAll = true;
            for (int i = 0; i < frontSol.length; i++) {
                int comp = compareDouble(solution[i], frontSol[i]);
                if (comp < 0) {
                    // solution[i] es mejor que frontSol[i]
                    worstInAll = false;
                } else if (comp > 0)  {
                    // frontSol[i] es mejor que solution[i]
                    bestInAll = false;
                }
            }
            if (worstInAll) {
                // Solucion dominada, no entra (o ya esta en el frente, no entra)
                enter = false;
                break;
            }
            if (bestInAll) {
                dominated.add(idx);
            }
            idx++;
        }
        int removed = 0;
        for (int idRem : dominated) {
            front.remove(idRem-removed);
            removed++;
        }
        if (enter) {
            front.add(solution.clone());
        }

        return enter;
    }

    public String toText() {
        StringBuilder stb = new StringBuilder();
        for (double[] sol : front) {
            for (double obj : sol) {
                stb.append(obj).append("\t");
            }
            stb.replace(stb.length()-1, stb.length(), "\n");
        }
        return stb.toString();
    }

    public void saveToFile(String path) {
        if (path.lastIndexOf('/') > 0) {
            File folder = new File(path.substring(0, path.lastIndexOf('/')));
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(path);
            pw.print(toText());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String path) {
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = bf.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                String[] tokens = line.split("\\s+");
                double[] objs = new double[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    objs[i] = Double.parseDouble(tokens[i]);
                    if (!minimizing[i]) {
                        objs[i] *= -1; // Para que sea siempre minimizar
                    }
                }
                add(objs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        boolean[] minimizing = new boolean[]{true, false, true};
        Pareto pareto = new Pareto(minimizing);
        pareto.loadFromFile("small1_nuestro.txt");
        pareto.loadFromFile("small1_moros.txt");
//        pareto.add(new double[]{10.5, 5.5, 15.4});
//        pareto.add(new double[]{10.6, 5.5, 15.4});
//        pareto.add(new double[]{10.4, 5.5, 15.4});
//        pareto.add(new double[]{10.5, 5.9, 15.4});
        System.out.println(pareto.toText());
    }
}
