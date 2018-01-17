package narcolepticfrog.rsmm.meterable.mixins;

import narcolepticfrog.rsmm.meterable.Meterable;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockAir.class)
public abstract class MixinBlockAirMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        return false;
    }

}
