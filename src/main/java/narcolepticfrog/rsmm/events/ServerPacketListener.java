package narcolepticfrog.rsmm.events;

import net.minecraft.network.PacketBuffer;

public interface ServerPacketListener {

    void onCustomPayload(String channel, PacketBuffer data);

}
