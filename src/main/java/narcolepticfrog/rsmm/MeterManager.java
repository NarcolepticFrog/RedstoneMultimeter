package narcolepticfrog.rsmm;

import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeterManager {

    private List<Meter> meters = new CopyOnWriteArrayList<>(); // Todo: Think of a better concurrency solution.
    private int nameCounter = 0;

    public void toggleMeter(BlockPos pos, int dimension, int duration) {
        boolean anyRemoved = meters.removeIf(m -> m.getPosition().equals(pos) && m.getDimension() == dimension);
        if (!anyRemoved) {
            Meter m = new Meter(pos, dimension, "Meter " + nameCounter++, duration, RandomColors.randomColor());
            meters.add(m);
        }
    }

    public List<Meter> getMeters() {
        return meters;
    }

    public void removeAll() {
        meters.clear();
        nameCounter = 0;
    }

}
