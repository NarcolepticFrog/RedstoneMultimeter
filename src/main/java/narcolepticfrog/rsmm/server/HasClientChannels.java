package narcolepticfrog.rsmm.server;

import java.util.Set;

public interface HasClientChannels {

    void addClientChannel(String name);

    void removeClientChannel(String name);

    Set<String> getClientChannels();

}
