package narcolepticfrog.rsmm.events;

import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public class ServerPacketEventDispatcher {

    private ServerPacketEventDispatcher() {}

    private static List<ServerPacketEventListener> listeners = new ArrayList<>();

    public static void addListener(ServerPacketEventListener listener) {
        listeners.add(listener);
    }

    public static void dispatchCustomPayload(String channel, PacketBuffer data) {
        for (ServerPacketEventListener listener : listeners) {
            listener.onCustomPayload(channel, data);
        }
    }

}
