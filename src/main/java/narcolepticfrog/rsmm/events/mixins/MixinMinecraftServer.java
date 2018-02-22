package narcolepticfrog.rsmm.events.mixins;


import narcolepticfrog.rsmm.events.TickStartEventDispatcher;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Shadow
    public abstract int getTickCounter();

    @Inject(method = "updateTimeLightAndEntities()V", at = @At("HEAD"))
    private void onServerTick(CallbackInfo ci) {
        TickStartEventDispatcher.dispatchEvent(getTickCounter());
    }

}
