package narcolepticfrog.rsmm.server.mixins;

import io.netty.buffer.Unpooled;
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

    @Inject(method = "initializeConnectionToPlayer", at = @At("RETURN"))
    public void onInitializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn, CallbackInfo cb) {
        // Register the RSMM channel with the client.
        String channelName = "RSMM";
        playerIn.connection.sendPacket(new SPacketCustomPayload("REGISTER",
                new PacketBuffer(Unpooled.wrappedBuffer(channelName.getBytes(StandardCharsets.UTF_8)))));
    }

}

