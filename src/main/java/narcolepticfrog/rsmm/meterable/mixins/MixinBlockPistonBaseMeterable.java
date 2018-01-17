package narcolepticfrog.rsmm.meterable.mixins;

import narcolepticfrog.rsmm.meterable.Meterable;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBaseMeterable implements Meterable {

    @Shadow
    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) { return false; }

    @Override
    public boolean isPowered(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (source instanceof World) {
            World w = (World)source;
            return this.shouldBeExtended(w, pos, state.getValue(BlockDirectional.FACING));
        }
        return false;
    }

}
