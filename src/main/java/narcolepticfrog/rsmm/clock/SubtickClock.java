package narcolepticfrog.rsmm.clock;

import narcolepticfrog.rsmm.util.Trace;

import java.util.function.Function;

public class SubtickClock {

    public static final int HISTORY_LENGTH = 100000;

    private long tick = -1;
    private int length = 0;

    /**
     * The list of historical ticks that had non-zero length
     */
    private Trace<Long> ticks = new Trace<>(HISTORY_LENGTH);
    private Trace<Integer> tickLengths = new Trace<>(HISTORY_LENGTH);

    /**
     * Returns the current tick.
     */
    public long getTick() {
        return tick;
    }

    /**
     * Registers a new subtick time with the clock. If this time occurred during the current tick, then update
     * the length of this tick. Otherwise, it must have occurred after this tick, in which case we
     * @param time
     */
    public void registerTime(SubtickTime time) {
        if (time.getTick() == tick) {
            length = Math.max(length, time.getSubtickIndex()+1);
        } else if (time.getTick() > tick) {
            if (length > 0) {
                ticks.push(tick);
                tickLengths.push(length);
            }
            tick = time.getTick();
            length = time.getSubtickIndex()+1;
        }
    }

    /**
     * Returns the number of SubtickTimes used during the given tick.
     */
    public int tickLength(long tick) {
        if (tick == this.tick) {
            return this.length;
        }
        int ix = ticks.binarySearch(tick, Function.identity());
        if (0 <= ix && ix < tickLengths.size() && ticks.get(ix) == tick) {
            return tickLengths.get(ix);
        }
        return 0;
    }

    /**
     * Returns the first subtick time within the tick.
     */
    public synchronized SubtickTime firstTimeOfTick(long tick) {
        return new SubtickTime(tick, 0);
    }

    /**
     * Returns the last taken subtick time within the tick.
     */
    public synchronized SubtickTime lastTimeOfTick(long tick) {
        return new SubtickTime(tick, tickLength(tick) - 1);
    }

}
