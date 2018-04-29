package narcolepticfrog.rsmm.events;

import narcolepticfrog.rsmm.clock.SubtickTime;

import java.util.ArrayList;
import java.util.List;

public class TickEventDispatcher {

    private TickEventDispatcher() {}

    private static List<TickEventListener> listeners = new ArrayList<>();

    public static void addListener(TickEventListener listener) {
        listeners.add(listener);
    }

    public static void dispatchTickStart(int tick) {
        for (TickEventListener listener : listeners) {
            listener.onTickStart(tick);
        }
    }

    public static void dispatchPhase(SubtickTime.TickPhase phase) {
        for (TickEventListener listener : listeners) {
            listener.onTickPhase(phase);
        }
    }

}
