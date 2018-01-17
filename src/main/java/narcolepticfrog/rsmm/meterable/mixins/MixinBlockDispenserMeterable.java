package narcolepticfrog.rsmm.meterable.mixins;

import narcolepticfrog.rsmm.meterable.Meterable;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockDispenser.class)
public abstract class MixinBlockDispenserMeterable implements Meterable {

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (source instanceof World) {
            World w = (World)source;
            return w.isBlockPowered(pos) || w.isBlockPowered(pos.up());
        }
        return false;
    }

}
