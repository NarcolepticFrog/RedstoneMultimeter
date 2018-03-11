package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.events.PistonPushEventDispatcher;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {

    private final static String REDIRECT_TARGET = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;";

    @Redirect(method = "doMove", at = @At(value = "INVOKE", target=REDIRECT_TARGET, ordinal=2))
    private IBlockState getBlockStateProxy(World w, BlockPos pos,
                                           World world, BlockPos pistonPos, EnumFacing direction, boolean extending) {
        if (!w.isRemote) {
            PistonPushEventDispatcher.dispatchEvent(w, pos, extending ? direction : direction.getOpposite());
        }
        return w.getBlockState(pos);
    }

}
