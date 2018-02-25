package narcolepticfrog.rsmm.events.mixins;

import io.netty.buffer.Unpooled;
import narcolepticfrog.rsmm.events.PlayerConnectionEventDispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method="playerLoggedIn", at = @At("RETURN"))
    public void onPlayerLoggedIn(EntityPlayerMP player, CallbackInfo cb) {
        PlayerConnectionEventDispatcher.dispatchPlayerConnectEvent(player);
    }

}

