package narcolepticfrog.rsmm.events;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface NeighborChangeListener {

    /**
     * Gets called each time a block update occurs.
     */
    void onNeighborChanged(World world, BlockPos pos, Block block, BlockPos neighbor);

}
