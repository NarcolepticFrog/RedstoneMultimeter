package narcolepticfrog.rsmm.server.mixins;

import com.google.common.base.Charsets;
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

    private final static String CHANNEL_SEPARATOR = "\u0000";
    private final static String REGISTER_CHANNELS = "REGISTER";
    private final static String UNREGISTER_CHANNELS = "UNREGISTER";

    private static String getChannelsString(PacketBuffer buff) {
        byte[] bytes = new byte[buff.readableBytes()];
        buff.readBytes(bytes);
        return new String(bytes, Charsets.UTF_8);
    }

    @Shadow public EntityPlayerMP player;

    @Inject(method = "processCustomPayload", at = @At("HEAD"))
    public void onProcessCustomPayload(CPacketCustomPayload packetIn, CallbackInfo cb) {
        String channelName = packetIn.getChannelName();
        PacketBuffer data = packetIn.getBufferData();
        HasClientChannels client = (HasClientChannels)this.player;
        if (channelName.equals(REGISTER_CHANNELS)) {
            for (String channel : getChannelsString(data).split(CHANNEL_SEPARATOR)) {
                client.addClientChannel(channel);
            }
        } else if (channelName.equals(UNREGISTER_CHANNELS)) {
            for (String channel : getChannelsString(data).split(CHANNEL_SEPARATOR)) {
                client.removeClientChannel(channel);
            }
        }
    }

}

