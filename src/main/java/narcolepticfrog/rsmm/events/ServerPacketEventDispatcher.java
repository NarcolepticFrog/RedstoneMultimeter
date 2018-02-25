package narcolepticfrog.rsmm.events;

import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public class ServerPacketEventDispatcher {

    private ServerPacketEventDispatcher() {}

    private static List<ServerPacketListener> listeners = new ArrayList<>();

    public static void addListener(ServerPacketListener listener) {
        listeners.add(listener);
    }

    public static void dispatchCustomPayload(String channel, PacketBuffer data) {
        for (ServerPacketListener listener : listeners) {
            listener.onCustomPayload(channel, data);
        }
    }

}
