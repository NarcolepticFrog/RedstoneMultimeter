package narcolepticfrog.rsmm.clock;

import narcolepticfrog.rsmm.events.TickStartEventDispatcher;
import narcolepticfrog.rsmm.events.TickStartListener;
import narcolepticfrog.rsmm.util.Trace;

public class SubtickClock {

    public static final int HISTORY_LENGTH = 100000;

    private int tick = -1;
    private Trace<Integer> tickLengths = new Trace<>(HISTORY_LENGTH);

    /**
     * Returns the current tick.
     */
    public int getTick() {
        return tick;
    }

    /**
     * Should be called at the start of every tick.
     */
    public void onTickStart(int tick, int lastTickLength) {
        this.tick = tick;
        tickLengths.push(lastTickLength);
    }

    /**
     * Returns the number of SubtickTimes used during the given tick.
     */
    public int tickLength(int tick) {
        int ix = this.tick - 1 - tick;
        if (0 <= ix && ix < tickLengths.size()) {
            return tickLengths.get(ix);
        }
        return 0;
    }

    /**
     * Returns the first subtick time within the tick.
     */
    public synchronized SubtickTime firstTimeOfTick(int tick) {
        return new SubtickTime(tick, 0);
    }

    /**
     * Returns the last taken subtick time within the tick.
     */
    public synchronized SubtickTime lastTimeOfTick(int tick) {
        return new SubtickTime(tick, tickLength(tick) - 1);
    }

}
