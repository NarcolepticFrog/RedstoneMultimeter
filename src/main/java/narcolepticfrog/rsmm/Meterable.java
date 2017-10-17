package narcolepticfrog.rsmm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface Meterable {

    boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos);

}
