package narcolepticfrog.rsmm.clock;

import narcolepticfrog.rsmm.util.Trace;

public class SubtickClock {

    public static final int HISTORY_LENGTH = 100000;

    private int tick;
    private int subtickIndex;
    private Trace<Integer> tickLengths = new Trace<>(HISTORY_LENGTH);

    /**
     * Returns the next SubtickTime for the current tick.
     */
    public synchronized SubtickTime takeNextTime() {
        return new SubtickTime(tick, subtickIndex++);
    }

    /**
     * Returns the current tick.
     */
    public synchronized int getTick() {
        return tick;
    }

    /**
     * Should be called at the start of every tick.
     */
    public synchronized void startTick(int tick) {
        this.tick = tick;
        tickLengths.push(subtickIndex);
        subtickIndex = 0;
    }

    /**
     * Returns the number of SubtickTimes used during the given tick.
     */
    public synchronized int tickLength(int tick) {
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

    /* ----- Singleton ----- */

    private SubtickClock() {}

    private static SubtickClock singleton = new SubtickClock();

    public static SubtickClock getClock() {
        return singleton;
    }

}
