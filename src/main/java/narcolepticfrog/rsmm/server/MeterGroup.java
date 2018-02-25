package narcolepticfrog.rsmm.server;

import io.netty.buffer.Unpooled;
import narcolepticfrog.rsmm.ColorUtils;
import narcolepticfrog.rsmm.DimPos;
import narcolepticfrog.rsmm.clock.SubtickTime;
import narcolepticfrog.rsmm.meterable.Meterable;
import narcolepticfrog.rsmm.network.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class MeterGroup implements RSMMSPacketHandler {

    private String name;
    private RSMMServer server;
    private HashSet<UUID> subscribedPlayers = new HashSet<>();

    public MeterGroup(RSMMServer server, String name) {
        this.server = server;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public EntityPlayerMP getPlayerFromUUID(UUID uuid) {
        Entity playerEntity = server.getMinecraftServer().getEntityFromUuid(uuid);
        if (playerEntity != null && playerEntity instanceof EntityPlayerMP) {
            return (EntityPlayerMP)playerEntity;
        }
        return null;
    }

    public void addPlayer(UUID playerUUID) {
        subscribedPlayers.add(playerUUID);
        EntityPlayerMP player = getPlayerFromUUID(playerUUID);
        server.sendToPlayer(player, new RSMMCPacketMeterGroup(name));
        for (int meterId = 0; meterId < meters.size(); meterId++) {
            Meter meter = meters.get(meterId);

            RSMMCPacketMeter packet = new RSMMCPacketMeter();
            packet.setDimpos(meter.dimpos);
            packet.setName(meter.name);
            packet.setColor(meter.color);
            packet.setPowered(meter.powered);
            packet.setCreate();

            server.sendToPlayer(player, packet);
        }
    }

    public void removePlayer(UUID playerUUID) {
        subscribedPlayers.remove(playerUUID);
    }

    public void broadcastToSubscribers(RSMMCPacket packet) {
        for (UUID playerUUID : subscribedPlayers) {
            Entity playerEntity = server.getMinecraftServer().getEntityFromUuid(playerUUID);
            if (playerEntity != null && playerEntity instanceof EntityPlayerMP) {
                server.sendToPlayer((EntityPlayerMP)playerEntity, packet);
            }
        }
    }

    private int currentTick = -1;
    private int subtickIndex = 0;

    private SubtickTime takeNextTime() {
        return new SubtickTime(currentTick, subtickIndex++);
    }

    public void onTickStart(int tick) {
        RSMMCPacketClock packet = new RSMMCPacketClock(tick, subtickIndex);
        broadcastToSubscribers(packet);
        currentTick = tick;
        subtickIndex = 0;
    }

    /**
     * This is the server's representation of a meter. Since the server does not store histories,
     * this class is significantly simpler than the Meter class used by the client.
     */
    private static class Meter {
        public DimPos dimpos;
        public boolean powered;
        public String name;
        public int color;
        public boolean movable;
    }

    private int meterNameIndex = 0;
    private ArrayList<Meter> meters = new ArrayList<>();
    private HashMap<DimPos, Integer> dimpos2index = new HashMap<>();

    public int getNumMeters() {
        return meters.size();
    }

    public void onPistonPush(World w, BlockPos pos, EnumFacing direction) {
        int dim = w.provider.getDimensionType().getId();
        DimPos dimpos = new DimPos(dim, pos);
        if (dimpos2index.containsKey(dimpos)) {
            int meterId = dimpos2index.get(dimpos);
            Meter meter = meters.get(meterId);
            if (meter.movable) {
                DimPos newDimPos = dimpos.offset(direction);

                meter.dimpos = newDimPos;
                dimpos2index.remove(dimpos);
                dimpos2index.put(newDimPos, meterId);

                RSMMCPacketMeter packet = new RSMMCPacketMeter();
                packet.setMeterId(meterId);
                packet.setDimpos(dimpos);
                broadcastToSubscribers(packet);
            }
        }
    }

    public void onStateChange(World w, BlockPos pos) {
        int dim = w.provider.getDimensionType().getId();
        DimPos dimpos = new DimPos(dim, pos);
        if (dimpos2index.containsKey(dimpos)) {
            int meterId = dimpos2index.get(dimpos);
            Meter meter = meters.get(meterId);

            IBlockState blockState = w.getBlockState(pos);
            Meterable meterable = (Meterable)blockState.getBlock();

            boolean powered = meterable.isPowered(blockState, w, pos);
            if (powered != meter.powered) {
                meter.powered = powered;
                SubtickTime time = takeNextTime();

                RSMMCPacketMeter packet = new RSMMCPacketMeter();
                packet.setMeterId(meterId);
                packet.setTime(time);
                packet.setPowered(powered);
                broadcastToSubscribers(packet);
            }
        }
    }

    @Override
    public void handleToggleMeter(RSMMSPacketToggleMeter packet) {
        DimPos dimpos = packet.getDimpos();
        if (!dimpos2index.containsKey(dimpos)) {
            // Create a new meter
            String name = "Meter " + (meterNameIndex++);
            int color = ColorUtils.nextColor();
            boolean movable = packet.isMovable();
            World w = server.getMinecraftServer().getWorld(dimpos.getDim());
            IBlockState blockState = w.getBlockState(dimpos.getPos());
            Meterable meterable = (Meterable)blockState.getBlock();
            boolean powered = meterable.isPowered(blockState, w, dimpos.getPos());

            Meter m = new Meter();
            m.name = name;
            m.color = color;
            m.movable = movable;
            m.dimpos = dimpos;
            m.powered = powered;
            meters.add(m);
            dimpos2index.put(dimpos, meters.size() - 1);

            RSMMCPacketMeter outPacket = new RSMMCPacketMeter();
            outPacket.setName(name);
            outPacket.setColor(color);
            outPacket.setDimpos(dimpos);
            outPacket.setPowered(powered);
            outPacket.setCreate();
            broadcastToSubscribers(outPacket);
        } else {
            // Remove the old meter
            int meterId = dimpos2index.get(dimpos);
            dimpos2index.remove(dimpos);
            meters.remove(meterId);
            for (int i = 0; i < meters.size(); i++) {
                dimpos2index.put(meters.get(i).dimpos, i);
            }
            RSMMCPacketMeter outPacket = new RSMMCPacketMeter();
            outPacket.setMeterId(meterId);
            outPacket.setDelete();
            broadcastToSubscribers(outPacket);
        }
    }

    public void renameMeter(int meterId, String name) {
        if (meterId < 0 || meterId >= meters.size()) return;
        meters.get(meterId).name = name;
        RSMMCPacketMeter packet = new RSMMCPacketMeter();
        packet.setMeterId(meterId);
        packet.setName(name);
        broadcastToSubscribers(packet);
    }

    public void renameLastMeter(String name) {
        int meterId = meters.size() - 1;
        renameMeter(meterId, name);
    }

    public void recolorMeter(int meterId, int color) {
        if (meterId < 0 || meterId >= meters.size()) return;
        meters.get(meterId).color = color;
        RSMMCPacketMeter packet = new RSMMCPacketMeter();
        packet.setMeterId(meterId);
        packet.setColor(color);
        broadcastToSubscribers(packet);
    }

    public void recolorLastMeter(int color) {
        int meterId = meters.size() - 1;
        recolorMeter(meterId, color);
    }

    public void removeAllMeters() {
        for (int meterId = meters.size() - 1; meterId >= 0; meterId--) {
            RSMMCPacketMeter packet = new RSMMCPacketMeter();
            packet.setMeterId(meterId);
            packet.setDelete();
            broadcastToSubscribers(packet);
        }
        meters.clear();
        dimpos2index.clear();
    }

}
