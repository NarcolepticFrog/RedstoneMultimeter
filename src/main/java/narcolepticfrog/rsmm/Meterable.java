package narcolepticfrog.rsmm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * This interface is used for determining whether a given block is powered or not. Mixins
 * are used to implement this for the Block class hierarchy.
 */
public interface Meterable {

    boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos);

}
