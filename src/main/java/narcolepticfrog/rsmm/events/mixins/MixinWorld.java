package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.clock.SubtickTime;
import narcolepticfrog.rsmm.events.StateChangeEventDispatcher;
import narcolepticfrog.rsmm.events.TickEventDispatcher;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class MixinWorld {

    private static final String PROFILER_ESS = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V";

    @Final
    public boolean isRemote;

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

    @Inject(method = "updateEntities", at = @At(value = "INVOKE_STRING", target = PROFILER_ESS, args = "ldc=regular"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (!this.isRemote) {
            TickEventDispatcher.dispatchPhase(SubtickTime.TickPhase.ENTITIES);
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "INVOKE_STRING", target = PROFILER_ESS, args = "ldc=blockEntities"))
    private void onUpdateTileEntities(CallbackInfo ci) {
        if (!this.isRemote) {
            TickEventDispatcher.dispatchPhase(SubtickTime.TickPhase.TILE_ENTITIES);
        }
    }

}
