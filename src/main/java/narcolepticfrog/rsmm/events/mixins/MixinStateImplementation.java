package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.events.NeighborChangeEventDispatcher;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.block.state.BlockStateContainer.StateImplementation")
public abstract class MixinStateImplementation {

    @Inject(method = "neighborChanged", at = @At("RETURN"))
    public void onNeighborChanged(World world, BlockPos pos, Block b, BlockPos neighbor,
                                  CallbackInfo ci) {
        if (!world.isRemote) {
            NeighborChangeEventDispatcher.dispatchEvent(world, pos, b, neighbor);
        }
    }

}
