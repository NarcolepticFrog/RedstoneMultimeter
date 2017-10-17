package narcolepticfrog.rsmm.mixins;

import narcolepticfrog.rsmm.Meterable;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPressurePlateWeighted.class)
public abstract class MixinBlockPressurePlateWeightedMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(BlockPressurePlateWeighted.POWER) > 0;
    }

}
