package narcolepticfrog.rsmm.mixins;

import narcolepticfrog.rsmm.Meterable;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRedstoneWire.class)
public abstract class MixinBlockRedstoneWireMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(BlockRedstoneWire.POWER) > 0;
    }

}
