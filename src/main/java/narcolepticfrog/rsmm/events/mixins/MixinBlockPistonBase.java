package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.events.PistonPushEventDispatcher;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {

    private static final String METHOD_INVOKE_ASSIGN = "net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;";

    @Inject(method = "doMove", at = @At("HEAD"))
    public void onBlockMove(World world, BlockPos pos, EnumFacing facing, boolean extending, CallbackInfoReturnable<Boolean> ci) {
        if (!world.isRemote) {
            BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(world, pos, facing, extending);
            if (blockpistonstructurehelper.canMove()) {
                EnumFacing pushDirection = extending ? facing : facing.getOpposite();
                List<BlockPos> blocksToMove = blockpistonstructurehelper.getBlocksToMove();
                Collections.reverse(blocksToMove);
                for (BlockPos p : blocksToMove) {
                    PistonPushEventDispatcher.dispatchEvent(world, p, pushDirection);
                }
            }
        }
    }

}
