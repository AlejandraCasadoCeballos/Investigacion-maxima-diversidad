package grafo.moflp.structure;

public class Candidate {
    public double x;
    public double y;

    public Candidate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}