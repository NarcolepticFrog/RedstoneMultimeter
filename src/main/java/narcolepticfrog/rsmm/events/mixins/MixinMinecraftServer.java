package narcolepticfrog.rsmm.events.mixins;


import narcolepticfrog.rsmm.clock.SubtickTime;
import narcolepticfrog.rsmm.events.TickEventDispatcher;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    private static final String PROFILER_SS = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V";

    @Shadow
    public int getTickCounter() {
        return 0;
    }

    @Inject(method = "updateTimeLightAndEntities()V", at = @At("HEAD"))
    private void onServerTick(CallbackInfo ci) {
        TickEventDispatcher.dispatchTickStart(getTickCounter());
    }

    @Inject(method = "updateTimeLightAndEntities()V", at = @At(value = "INVOKE_STRING", target = PROFILER_SS, args = "ldc=jobs"))
    private void onProcessFutureJobs(CallbackInfo ci) {
        TickEventDispatcher.dispatchPhase(SubtickTime.TickPhase.PLAYERS);
    }



}
