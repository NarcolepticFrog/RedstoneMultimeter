package narcolepticfrog.rsmm;

import narcolepticfrog.rsmm.clock.SubtickClock;
import narcolepticfrog.rsmm.clock.SubtickTime;
import narcolepticfrog.rsmm.util.Trace;

import java.util.function.Consumer;

public class Meter {

    public static final int MAX_STATE_CHANGES = 10000;

    private SubtickClock clock;
    private DimPos dimpos;
    private String name;
    private int color;
    private Trace<StateChange> stateChanges;
    private Trace<SubtickTime> moveTimes;
    private boolean movable;

    public Meter(SubtickClock clock, DimPos dimpos, String name, int color, boolean movable) {
        this.clock = clock;
        this.dimpos = dimpos;
        this.name = name;
        this.stateChanges = new Trace<>(MAX_STATE_CHANGES);
        this.moveTimes = new Trace<>(MAX_STATE_CHANGES);
        this.color = color;
        this.movable = movable;
    }

    public boolean isMovable() {
        return movable;
    }

    public void registerStateChange(SubtickTime time, boolean isPowered) {
        stateChanges.push(new StateChange(time, isPowered));
    }

    public void registerMove(SubtickTime time, DimPos newDimPos) {
        this.dimpos = newDimPos;
        this.moveTimes.push(time);
    }

    public StateChange getStateChange(SubtickTime time) {
        int ix = stateChanges.binarySearch(time, StateChange::getTime);
        if (ix >= 0 && ix < stateChanges.size() && stateChanges.get(ix).getTime().equals(time)) {
            return stateChanges.get(ix);
        }
        return null;
    }

    public StateChange mostRecentChange(SubtickTime time) {
        int ix = stateChanges.binarySearch(time, StateChange::getTime);
        if (ix == -1) {
            return null;
        }
        return stateChanges.get(ix);
    }

    public int stateDuration(SubtickTime t) {
        int ix = stateChanges.binarySearch(t, StateChange::getTime);
        if (ix <= 0) {
            return -1;
        }
        int startTick = stateChanges.get(ix).getTime().getTick();
        int endTick = stateChanges.get(ix-1).getTime().getTick();
        return endTick - startTick;
    }

    /**
     * Returns true if the meter was powered at the given SubtickTime.
     */
    public boolean wasPoweredAt(SubtickTime time) {
        int ix = stateChanges.binarySearch(time, StateChange::getTime);
        if (ix == -1) {
            return false;
        }
        return stateChanges.get(ix).getState();
    }

    /**
     * Returns true if the meter was powered when the given tick started.
     */
    public boolean wasPoweredAtStart(int tick) {
        return wasPoweredAt(clock.lastTimeOfTick(tick-1));
    }

    /**
     * Returns true if the meter was powered before the start of this tick, and had no state changes during the tick.
     */
    public boolean wasPoweredEntireTick(int tick) {
        if (!wasPoweredAtStart(tick)) {
            return false;
        }
        int ix = stateChanges.binarySearch(clock.lastTimeOfTick(tick-1), StateChange::getTime);
        return ix <= 0 || stateChanges.get(ix-1).getTime().compareTo(clock.lastTimeOfTick(tick)) > 0;
    }

    /**
     * Returns true if the meter was powered for any SubtickTime during the given tick.
     */
    public boolean wasPoweredDuring(int tick) {
        if (wasPoweredAtStart(tick)) {
            return true;
        }
        SubtickTime start = clock.lastTimeOfTick(tick-1);
        SubtickTime end = clock.lastTimeOfTick(tick);
        boolean[] powered = {false};
        forEachChange(start, end, sc -> powered[0] |= sc.getState());
        return powered[0];
    }

    /**
     * Executes function f for each state changes that occurred between SubtickTimes a and b (inclusively).
     */
    public void forEachChange(SubtickTime a, SubtickTime b, Consumer<StateChange> f) {
        int ix = stateChanges.binarySearch(a, StateChange::getTime);
        if (ix == -1) {
            ix = stateChanges.size() - 1;
        }
        while (ix >= 0 && stateChanges.get(ix).getTime().compareTo(b) <= 0) {
            f.accept(stateChanges.get(ix));
            ix -= 1;
        }
    }

    /**
     * Returns true if this meter moved at the given subtick time.
     */
    public boolean movedAtTime(SubtickTime t) {
        int ix = moveTimes.binarySearch(t, x -> x);
        if (ix == -1) {
            return false;
        }
        return moveTimes.get(ix).equals(t);
    }

    public boolean movedDuring(int tick) {
        SubtickTime lastTime = clock.lastTimeOfTick(tick-1);
        int ix = moveTimes.binarySearch(lastTime, x -> x);
        if (ix <= 0) {
            return moveTimes.size() > 0 && moveTimes.get(moveTimes.size()-1).getTick() == tick;
        }
        return moveTimes.get(ix-1).getTick() == tick;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public DimPos getDimPos() {
        return dimpos;
    }

    public void setDimPos(DimPos dimpos) {
        this.dimpos = dimpos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDimension() {
        return dimpos.getDim();
    }

    public static class StateChange {
        private SubtickTime time;
        private boolean state;

        public StateChange(SubtickTime time, boolean newState) {
            this.time = time;
            this.state = newState;
        }

        public SubtickTime getTime() {
            return time;
        }

        public boolean getState() {
            return state;
        }
    }

}
