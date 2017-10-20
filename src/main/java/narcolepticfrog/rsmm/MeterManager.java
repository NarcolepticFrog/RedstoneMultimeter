package narcolepticfrog.rsmm;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeEnd;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeterManager {

    private static int DEFAULT_MAX_INTERVALS = 1000;

    // This class is just used as a key into our Location to Meter map. We don't
    // want to confuse meters in different worlds with one another.
    private static class Location {
        int dim;
        BlockPos p;

        public Location(World w, BlockPos p) {
            this.dim = w.provider.getDimensionType().getId();
            this.p = p;
        }

        public int hashCode() {
            return Integer.hashCode(dim) ^ p.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Location) {
                Location o = (Location)obj;
                return o.dim == this.dim && o.p.equals(this.p);
            }
            return false;
        }
    }

    private ArrayList<Meter> meters = new ArrayList<>();
    private Map<Location, Meter> location2meter = new HashMap<>();
    private int nameCounter = 0;

    public void toggleMeter(BlockPos pos, World world) {
        Location l = new Location(world, pos);
        if (location2meter.containsKey(l)) {
            Meter m = location2meter.get(l);
            location2meter.remove(l);
            meters.remove(m);
        } else {
            Meter m = new Meter(pos, world, "Meter " + nameCounter++, DEFAULT_MAX_INTERVALS, RandomColors.randomColor());
            location2meter.put(l, m);
            meters.add(m);
        }
    }

    public List<Meter> getMeters() {
        return meters;
    }

    public void removeAll() {
        meters.clear();
        location2meter.clear();
        nameCounter = 0;
    }

    public void onPistonPush(World w, BlockPos pos, EnumFacing direction) {
        Location l = new Location(w, pos);
        if (location2meter.containsKey(l)) {
            Meter m = location2meter.get(l);
            BlockPos newPos = pos.offset(direction);
            m.setPosition(newPos);
            location2meter.remove(l);
            location2meter.put(new Location(w, newPos), m);
        }
    }
}
