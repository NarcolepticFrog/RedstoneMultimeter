package narcolepticfrog.rsmm.events;

import net.minecraft.entity.player.EntityPlayerMP;

public interface PlayerConnectionEventListener {

    public void onPlayerConnect(EntityPlayerMP player);

}
