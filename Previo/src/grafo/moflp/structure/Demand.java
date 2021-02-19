package grafo.moflp.structure;

public class Demand {
    public double x;
    public double y;
    public float w;

    public Demand(double x, double y, float w) {
        this.x = x;
        this.y = y;
        this.w = w;
    }

    @Override
    public String toString() {
        return "("+x+","+y+","+w+")";
    }
}
