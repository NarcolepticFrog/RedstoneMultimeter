package narcolepticfrog.rsmm.mixins;

import narcolepticfrog.rsmm.PistonPushEventDispatcher;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {

    private static final String METHOD_INVOKE_ASSIGN = "net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;";

    @Inject(method = "doMove",
            at = @At(value = "INVOKE_ASSIGN", target = METHOD_INVOKE_ASSIGN, ordinal = 2),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void onBlockMove(World world, BlockPos pos, EnumFacing facing, boolean extending, CallbackInfoReturnable<Boolean> ci,
                            BlockPistonStructureHelper helper, List l1, List l2, List l3, int k, IBlockState[] starr,
                            EnumFacing pushDir, int l, BlockPos moveBlock, IBlockState moveBlockState) {
        if (!world.isRemote) {
            PistonPushEventDispatcher.dispatchEvent(world, moveBlock, pushDir);
        }
    }

}
