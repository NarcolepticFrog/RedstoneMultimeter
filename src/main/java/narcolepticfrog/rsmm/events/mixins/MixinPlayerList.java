package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.events.PlayerConnectionEventDispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method="playerLoggedIn", at = @At("RETURN"))
    public void onPlayerLoggedIn(EntityPlayerMP player, CallbackInfo cb) {
        PlayerConnectionEventDispatcher.dispatchPlayerConnectEvent(player);
    }

    @Inject(method="playerLoggedOut", at = @At("RETURN"))
    public void onPlayerLoggedOut(EntityPlayerMP player, CallbackInfo cb) {
        PlayerConnectionEventDispatcher.dispatchPlayerDisconnectEvent(player);
    }

}

