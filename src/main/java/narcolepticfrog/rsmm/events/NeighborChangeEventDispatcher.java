package narcolepticfrog.rsmm.events;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class NeighborChangeEventDispatcher {

    private NeighborChangeEventDispatcher() {}

    private static List<NeighborChangeListener> listeners = new ArrayList<>();

    public static void addListener(NeighborChangeListener listener) {
        listeners.add(listener);
    }

    public static void dispatchEvent(World world, BlockPos pos, Block block, BlockPos neighbor) {
        for (NeighborChangeListener listener : listeners) {
            listener.onNeighborChanged(world, pos, block, neighbor);
        }
    }

}
