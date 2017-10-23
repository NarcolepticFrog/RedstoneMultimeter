package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.events.StateChangeEventDispatcher;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class MixinWorld {

    @Shadow
    private boolean isRemote;

    @Inject(method = "neighborChanged", at = @At("RETURN"))
    public void onNeighborChange(BlockPos pos, final Block blockIn, BlockPos fromPos,
                                 CallbackInfo ci) {
        if (!this.isRemote) {
            StateChangeEventDispatcher.dispatchEvent((World)(Object)this, pos);
        }
    }

    @Inject(method = "updateComparatorOutputLevel", at = @At("RETURN"))
    public void onUpdateComparatorOutputLevel(BlockPos pos, Block blockIn,
                                              CallbackInfo ci) {
        if (!this.isRemote) {
            StateChangeEventDispatcher.dispatchEvent((World)(Object)this, pos);
        }
    }

    @Inject(method = "observedNeighborChanged", at = @At("RETURN"))
    public void onObservedNeighborChanged(BlockPos pos, final Block block, BlockPos neighbor,
                                          CallbackInfo ci) {
        if (!this.isRemote) {
            StateChangeEventDispatcher.dispatchEvent((World)(Object)this, pos);
        }
    }


}
