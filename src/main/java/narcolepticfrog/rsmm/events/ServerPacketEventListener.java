package narcolepticfrog.rsmm.events;

import net.minecraft.network.PacketBuffer;

public interface ServerPacketEventListener {

    void onCustomPayload(String channel, PacketBuffer data);

}
