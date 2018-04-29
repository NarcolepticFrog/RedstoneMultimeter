package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.clock.SubtickTime;
import narcolepticfrog.rsmm.events.TickEventDispatcher;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer {

    @Final
    public boolean isRemote;

    @Inject(method = "tickUpdates", at = @At("HEAD"))
    private void onTickPending(boolean runAllPending, CallbackInfoReturnable ci) {
        if (!this.isRemote) {
            TickEventDispatcher.dispatchPhase(SubtickTime.TickPhase.TILE_TICKS);
        }
    }

    @Inject(method = "updateBlocks", at = @At("HEAD"))
    private void onTickBlocks(CallbackInfo ci) {
        if (!this.isRemote) {
            TickEventDispatcher.dispatchPhase(SubtickTime.TickPhase.RANDOM_TICKS);
        }
    }

    @Inject(method = "sendQueuedBlockEvents", at = @At("HEAD"))
    private void onQeuedBlockEvents(CallbackInfo ci) {
        if (!this.isRemote) {
            TickEventDispatcher.dispatchPhase(SubtickTime.TickPhase.BLOCK_EVENTS);
        }
    }

}
