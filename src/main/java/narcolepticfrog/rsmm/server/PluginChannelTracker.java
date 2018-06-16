package narcolepticfrog.rsmm.server;

import com.google.common.collect.Collections2;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import narcolepticfrog.rsmm.events.PlayerConnectionEventDispatcher;
import narcolepticfrog.rsmm.events.PlayerConnectionListener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;
import java.util.UUID;

public class PluginChannelTracker implements PlayerConnectionListener {

    private static PluginChannelTracker singleton = new PluginChannelTracker();

    /**
     * Gets the singleton instance of the {@code PluginChannelTracker}.
     */
    public static PluginChannelTracker getChannelTracker() {
        return singleton;
    }

    private PluginChannelTracker() {
        PlayerConnectionEventDispatcher.addListener(this);
    }

    // A multimap from player UUIDs to the channels they are registered on
    private SetMultimap<UUID, String> uuid2channels = MultimapBuilder.hashKeys().hashSetValues().build();

    // A multimap from channel names to the UUIDs of players registered to that channel
    private SetMultimap<String, UUID> channel2uuids = MultimapBuilder.hashKeys().hashSetValues().build();

    /**
     * Returns the collection of channels {@code player} is registered to.
     */

    public Collection<String> getChannels(EntityPlayerMP player) {
        return uuid2channels.get(player.getUniqueID());
    }

    /**
     * Returns whether or not {@code player} is reigstered to {@code channel}.
     */
    public boolean isRegistered(EntityPlayerMP player, String channel) {
        return uuid2channels.containsEntry(player.getUniqueID(), channel);
    }

    /**
     * Returns the collection of players registered to {@code channel}. The {@code server} is used to look players up
     * by their UUID.
     */
    public Collection<EntityPlayerMP> getPlayers(MinecraftServer server, String channel) {
        return Collections2.transform(channel2uuids.get(channel),
                (UUID uuid) -> (EntityPlayerMP) server.getEntityFromUuid(uuid) );
    }

    /**
     * Registers {@code player} on {@code channel}.
     */
    public void register(EntityPlayerMP player, String channel) {
        uuid2channels.put(player.getUniqueID(), channel);
        channel2uuids.put(channel, player.getUniqueID());
    }

    /**
     * Unregisters {@code player} from {@code channel}.
     */
    public void unregister(EntityPlayerMP player, String channel) {
        uuid2channels.remove(player, channel);
        channel2uuids.remove(channel, player);
    }

    /**
     * Unregisters {@code player} from all channels.
     */
    public void unregisterAll(EntityPlayerMP player) {
        for (String channel : getChannels(player)) {
            channel2uuids.remove(channel, player.getUniqueID());
        }
        uuid2channels.removeAll(player.getUniqueID());
    }

    @Override
    public void onPlayerDisconnect(EntityPlayerMP player) {
        unregisterAll(player);
    }

    @Override
    public void onPlayerConnect(EntityPlayerMP player) {}

}
