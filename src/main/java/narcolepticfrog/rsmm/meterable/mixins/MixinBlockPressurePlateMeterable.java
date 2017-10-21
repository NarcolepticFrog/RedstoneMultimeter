package narcolepticfrog.rsmm.meterable.mixins;

import narcolepticfrog.rsmm.Meterable;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPressurePlate.class)
public abstract class MixinBlockPressurePlateMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(BlockPressurePlate.POWERED);
    }

}
