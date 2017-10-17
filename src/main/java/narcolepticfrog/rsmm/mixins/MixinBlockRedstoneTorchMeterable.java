package narcolepticfrog.rsmm.mixins;

import narcolepticfrog.rsmm.Meterable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRedstoneTorch.class)
public abstract class MixinBlockRedstoneTorchMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        return (Block)(Object)this == Blocks.REDSTONE_TORCH;
    }

}
