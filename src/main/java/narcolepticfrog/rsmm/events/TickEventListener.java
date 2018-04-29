package narcolepticfrog.rsmm.events;

import narcolepticfrog.rsmm.clock.SubtickTime;

public interface TickEventListener {

    /**
     * Gets called at the beginning of each server tick.
     */
    void onTickStart(int tick);

    /**
     * Gets called when the current tick phase changes.
     */
    void onTickPhase(SubtickTime.TickPhase phase);

}
