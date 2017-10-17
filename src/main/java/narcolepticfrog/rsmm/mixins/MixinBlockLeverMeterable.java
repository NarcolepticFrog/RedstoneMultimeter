package narcolepticfrog.rsmm.mixins;

import narcolepticfrog.rsmm.Meterable;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockLever.class)
public abstract class MixinBlockLeverMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(BlockLever.POWERED);
    }

}
