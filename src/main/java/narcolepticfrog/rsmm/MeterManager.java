package narcolepticfrog.rsmm;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeterManager {

    private ArrayList<Meter> meters = new ArrayList<>();
    private Map<DimPos, Meter> location2meter = new HashMap<>();
    private int nameCounter = 0;

    public void toggleMeter(BlockPos pos, World world, boolean movable) {
        DimPos l = new DimPos(world, pos);
        if (location2meter.containsKey(l)) {
            Meter m = location2meter.get(l);
            location2meter.remove(l);
            meters.remove(m);
        } else {
            Meter m = new Meter(pos, world, I18n.format("redstonemultimeter.ui.metername", nameCounter++), ColorUtils.nextColor(), movable);
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
        DimPos l = new DimPos(w, pos);
        if (location2meter.containsKey(l)) {
            Meter m = location2meter.get(l);
            if (m.isMovable()) {
                BlockPos newPos = pos.offset(direction);
                m.setPosition(newPos);
                location2meter.remove(l);
                location2meter.put(new DimPos(w, newPos), m);
            }
        }
    }

    public Meter getMeter(World world, BlockPos pos) {
        DimPos l = new DimPos(world, pos);
        if (location2meter.containsKey(l)) {
            return location2meter.get(l);
        }
        return null;
    }

}
