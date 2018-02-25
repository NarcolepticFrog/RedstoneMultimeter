package narcolepticfrog.rsmm.network;

public interface RSMMCPacketHandler {

    void handleClock(RSMMCPacketClock packet);

    void handleMeter(RSMMCPacketMeter packet);

    void handleMeterGroup(RSMMCPacketMeterGroup packet);

}
