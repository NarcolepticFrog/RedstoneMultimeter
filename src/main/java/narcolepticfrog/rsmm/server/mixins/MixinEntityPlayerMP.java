package narcolepticfrog.rsmm.server.mixins;

import com.google.common.collect.ImmutableSet;
import io.netty.buffer.Unpooled;
import narcolepticfrog.rsmm.server.HasClientChannels;
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
import java.util.HashSet;
import java.util.Set;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP implements HasClientChannels {

    private Set<String> channels = new HashSet<>();

    public void addClientChannel(String name) {
        channels.add(name);
    }

    public void removeClientChannel(String name) {
        channels.remove(name);
    }

    public Set<String> getClientChannels() {
        return ImmutableSet.copyOf(channels);
    }

}

