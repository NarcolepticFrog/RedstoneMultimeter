package narcolepticfrog.rsmm.network;

import io.netty.buffer.Unpooled;
import narcolepticfrog.rsmm.DimPos;
import net.minecraft.network.PacketBuffer;

/**
 * This packet is sent from the client to the server indicating that a meter should be toggled
 * at the provided DimPos.
 */
public class RSMMCPacketClock extends RSMMCPacket {

    public static final byte MESSAGE_ID = 0;

    private int tick;
    private int lastTickLength;

    public RSMMCPacketClock(int tick, int lastTickLength) {
        this.tick = tick;
        this.lastTickLength = lastTickLength;
    }

    public int getTick() {
        return tick;
    }

    public int getLastTickLength() {
        return lastTickLength;
    }

    public static RSMMCPacketClock fromBuffer(PacketBuffer buffer) {
        Byte messageId = buffer.readByte();
        assert messageId == MESSAGE_ID;

        int tick = buffer.readInt();
        int lastTickLength = buffer.readInt();

        return new RSMMCPacketClock(tick, lastTickLength);
    }

    @Override
    public PacketBuffer toBuffer() {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeByte(MESSAGE_ID);
        buffer.writeInt(tick);
        buffer.writeInt(lastTickLength);
        return buffer;
    }

    @Override
    public void process(RSMMCPacketHandler handler) {
        handler.handleClock(this);
    }

}
