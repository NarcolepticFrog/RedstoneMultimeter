package narcolepticfrog.rsmm.meterable.mixins;

import narcolepticfrog.rsmm.meterable.Meterable;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRailDetector.class)
public abstract class MixinBlockRailDetectorMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(BlockRailDetector.POWERED);
    }

}
