package momdp.constructive;

import momdp.Main;
import momdp.localSearch.ILocalSearch;
import momdp.structure.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IConstructive {

    List<ILocalSearch> localSearchObjs = new ArrayList<>();

    default IConstructive AddLocalSearchObjs(ILocalSearch[] ls){
        localSearchObjs.addAll(Arrays.asList(ls));
        return this;
    }

    void solve(Instance instance, int numSolutions);
    default String getName(){
        StringBuilder name = new StringBuilder(getClass().getSimpleName() + "_Seed_" + Main.seed + "_SolCount_" + Main.numSolutions);
        for (ILocalSearch ls: localSearchObjs) {
            name.append("_").append(ls.getClass().getSimpleName());
        }
        return name.toString();
    }
}
