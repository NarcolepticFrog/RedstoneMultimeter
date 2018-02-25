package narcolepticfrog.rsmm.events.mixins;

import com.google.common.base.Charsets;
import narcolepticfrog.rsmm.events.ServerPacketEventDispatcher;
import narcolepticfrog.rsmm.server.HasClientChannels;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    @Inject(method = "processCustomPayload", at = @At("RETURN"))
    public void onProcessCustomPayload(CPacketCustomPayload packetIn, CallbackInfo cb) {
        String channelName = packetIn.getChannelName();
        PacketBuffer data = packetIn.getBufferData();
        ServerPacketEventDispatcher.dispatchCustomPayload(channelName, data);
    }

}

