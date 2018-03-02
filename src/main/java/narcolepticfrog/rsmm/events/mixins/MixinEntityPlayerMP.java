package narcolepticfrog.rsmm.events.mixins;

import com.google.common.collect.ImmutableSet;
import narcolepticfrog.rsmm.server.HasClientChannels;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;

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

