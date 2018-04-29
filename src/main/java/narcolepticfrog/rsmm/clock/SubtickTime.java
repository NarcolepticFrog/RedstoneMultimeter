package narcolepticfrog.rsmm.clock;

import net.minecraft.network.PacketBuffer;

public class SubtickTime implements Comparable<SubtickTime> {

    public enum TickPhase {
        //black  red         green         blue          pink      teal
        PLAYERS, TILE_TICKS, RANDOM_TICKS, BLOCK_EVENTS, ENTITIES, TILE_ENTITIES
    }

    private long tick;
    private int subtickIndex;
    private TickPhase phase;

    public SubtickTime(long tick, int subtickIndex, TickPhase phase) {
        this.tick = tick;
        this.subtickIndex = subtickIndex;
        this.phase = phase;
    }

    public SubtickTime(long tick, int subtickIndex) {
        this(tick, subtickIndex, TickPhase.PLAYERS);
    }

    public SubtickTime(long tick) {
        this(tick, 0, TickPhase.PLAYERS);
    }

    public long getTick() {
        return tick;
    }

    public int getSubtickIndex() {
        return subtickIndex;
    }

    public TickPhase getPhase() {
        return phase;
    }

    @Override
    public int compareTo(SubtickTime o) {
        if (this.tick != o.tick) {
            return Long.compare(this.tick, o.tick);
        } else {
            return Integer.compare(this.subtickIndex, o.subtickIndex);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof SubtickTime) {
            SubtickTime ot = (SubtickTime) o;
            return compareTo(ot) == 0;
        }
        return false;
    }

    public String toString() {
        return "SubtickTime[tick = " + tick + ", subtick = " + subtickIndex + ", phase = " + phase.toString() + "]";
    }

    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeVarLong(tick);
        buffer.writeInt(subtickIndex);
        buffer.writeByte((byte)phase.ordinal());
    }

    public static SubtickTime readFromBuffer(PacketBuffer buffer) {
        long tick = buffer.readVarLong();
        int subtickIndex = buffer.readInt();
        TickPhase phase = TickPhase.values()[buffer.readByte()];
        return new SubtickTime(tick, subtickIndex, phase);
    }

}
