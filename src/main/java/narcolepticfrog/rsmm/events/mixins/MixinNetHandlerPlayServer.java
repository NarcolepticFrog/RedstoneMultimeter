package narcolepticfrog.rsmm.events.mixins;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import narcolepticfrog.rsmm.events.ServerPacketEventDispatcher;
import narcolepticfrog.rsmm.server.PluginChannelTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    @Shadow EntityPlayerMP player;

    private final static String CHANNEL_SEPARATOR = "\u0000";
    private final static String REGISTER_CHANNELS = "REGISTER";
    private final static String UNREGISTER_CHANNELS = "UNREGISTER";

    private static List<String> getChannels(PacketBuffer buff) {
        byte[] bytes = new byte[buff.readableBytes()];
        buff.readBytes(bytes);
        String channelString = new String(bytes, Charsets.UTF_8);
        List<String> channels = Lists.newArrayList(channelString.split(CHANNEL_SEPARATOR));
        return channels;
    }

    @Inject(method = "processCustomPayload", at = @At("RETURN"))
    public void onProcessCustomPayload(CPacketCustomPayload packetIn, CallbackInfo cb) {
        String channelName = packetIn.getChannelName();
        PacketBuffer data = packetIn.getBufferData();
        data.resetReaderIndex();

        data.resetReaderIndex();
        if (channelName.equals(REGISTER_CHANNELS)) {
            List<String> channels = getChannels(data);
            for (String channel : channels) {
                PluginChannelTracker.getChannelTracker().register(player, channel);
            }
            ServerPacketEventDispatcher.dispatchChannelRegister(player, channels);
        } else if (channelName.equals(UNREGISTER_CHANNELS)) {
            List<String> channels = getChannels(data);
            for (String channel : channels) {
                PluginChannelTracker.getChannelTracker().unregister(player, channel);
            }
            ServerPacketEventDispatcher.dispatchChannelUnregister(player, channels);
        } else {
            ServerPacketEventDispatcher.dispatchCustomPayload(player, channelName, data);
        }
    }

}

