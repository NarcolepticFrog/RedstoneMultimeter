package narcolepticfrog.rsmm;

import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeterManager {

    private List<Meter> meters = new CopyOnWriteArrayList<>();
    private Random rand = new Random(0);
    private int nameCounter = 0;

    public void toggleMeter(BlockPos pos, int dimension, int duration) {
        boolean anyRemoved = meters.removeIf(m -> m.getPosition().equals(pos) && m.getDimension() == dimension);
        if (!anyRemoved) {
            int color = 0xFF000000;
            color |= (rand.nextInt(150) + 106);
            color |= (rand.nextInt(150) + 106) << 8;
            color |= (rand.nextInt(150) + 106) << 16;
            Meter m = new Meter(pos, dimension, "Meter " + nameCounter++, duration, color);
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
