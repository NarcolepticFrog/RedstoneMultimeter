package narcolepticfrog.rsmm.events;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;

public class PlayerConnectionEventDispatcher {

    private PlayerConnectionEventDispatcher() {}

    private static List<PlayerConnectionEventListener> listeners = new ArrayList<>();

    public static void addListener(PlayerConnectionEventListener listener) {
        listeners.add(listener);
    }

    public static void dispatchPlayerConnectEvent(EntityPlayerMP player) {
        for (PlayerConnectionEventListener listener : listeners) {
            listener.onPlayerConnect(player);
        }
    }

}
