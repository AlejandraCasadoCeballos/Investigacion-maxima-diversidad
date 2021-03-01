package momdp.localSearch;

import momdp.structure.Solution;

public interface ILocalSearch {

    boolean firstImprovement=true;
    boolean localSearchSolution(Solution sol);
    String getName();
}
