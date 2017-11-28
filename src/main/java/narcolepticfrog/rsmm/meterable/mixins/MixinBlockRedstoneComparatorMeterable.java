package narcolepticfrog.rsmm.meterable.mixins;

import narcolepticfrog.rsmm.meterable.Meterable;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRedstoneComparator.class)
public abstract class MixinBlockRedstoneComparatorMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {

        if (state.getValue(BlockRedstoneComparator.MODE) == BlockRedstoneComparator.Mode.COMPARE) {
            return state.getValue(BlockRedstoneComparator.POWERED);
        } else {
            return ((TileEntityComparator) source.getTileEntity(pos)).getOutputSignal() > 0;
        }
    }

}
