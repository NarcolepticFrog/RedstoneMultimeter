package narcolepticfrog.rsmm.clock;

import narcolepticfrog.rsmm.util.Trace;

public class SubtickClock {

    public static final int HISTORY_LENGTH = 1000;

    private int tick;
    private int subtickIndex;
    private Trace<Integer> tickLengths = new Trace<>(HISTORY_LENGTH);

    /**
     * Returns the next SubtickTime for the current tick.
     */
    public SubtickTime takeNextTime() {
        return new SubtickTime(tick, subtickIndex++);
    }

    /**
     * Returns the current tick.
     */
    public int getTick() {
        return tick;
    }

    /**
     * Should be called at the start of every tick.
     */
    public void startTick(int tick) {
        this.tick = tick;
        tickLengths.push(subtickIndex);
        subtickIndex = 0;
    }

    /**
     * Returns the number of SubtickTimes used during the given tick.
     */
    public int tickLength(int tick) {
        int ix = this.tick - tick;
        if (0 <= ix && ix < tickLengths.size()) {
            return tickLengths.get(ix);
        }
        return 0;
    }

    /* ----- Singleton ----- */

    private SubtickClock() {}

    private static SubtickClock singleton = new SubtickClock();

    public static SubtickClock getClock() {
        return singleton;
    }

}
