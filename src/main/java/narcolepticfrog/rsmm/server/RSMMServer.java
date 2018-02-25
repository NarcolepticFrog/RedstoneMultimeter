package narcolepticfrog.rsmm.server;

import com.google.common.base.Charsets;
import io.netty.buffer.Unpooled;
import narcolepticfrog.rsmm.events.*;
import narcolepticfrog.rsmm.network.RSMMCPacket;
import narcolepticfrog.rsmm.network.RSMMSPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RSMMServer implements StateChangeListener, PistonPushListener, TickStartListener,
        PlayerConnectionListener, ServerPacketListener {

    public RSMMServer() {
        StateChangeEventDispatcher.addListener(this);
        PistonPushEventDispatcher.addListener(this);
        TickStartEventDispatcher.addListener(this);
        PlayerConnectionEventDispatcher.addListener(this);
        ServerPacketEventDispatcher.addListener(this);
    }

    private HashMap<String, MeterGroup> meterGroups = new HashMap<>();
    private HashMap<UUID, String> playerSubscriptions = new HashMap<>();

    private MeterGroup getMeterGroup(EntityPlayerMP player) {
        UUID playerUUID = player.getUniqueID();
        String groupName = playerSubscriptions.get(playerUUID);
        return meterGroups.get(groupName);
    }

    private MinecraftServer minecraftServer;

    public MinecraftServer getMinecraftServer() {
        if (minecraftServer == null) {
            minecraftServer = Minecraft.getMinecraft().getIntegratedServer();
        }
        return minecraftServer;
    }

    public void sendToPlayer(EntityPlayerMP player, RSMMCPacket packet) {
        HasClientChannels clientChannels = (HasClientChannels)player;
        if (clientChannels.getClientChannels().contains("RSMM")) {
            player.connection.sendPacket(new SPacketCustomPayload("RSMM", packet.toBuffer()));
        }
    }

    @Override
    public void onPistonPush(World w, BlockPos pos, EnumFacing direction) {
        for (MeterGroup mg : meterGroups.values()) {
            mg.onPistonPush(w, pos, direction);
        }
    }

    @Override
    public void onPlayerConnect(EntityPlayerMP player) {
        // Register the RSMM plugin channel with the player
        player.connection.sendPacket(new SPacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.wrappedBuffer("RSMM".getBytes(Charsets.UTF_8)))));
        // Give them a default group subscription
        if (!playerSubscriptions.containsKey(player.getUniqueID())) {
            playerSubscriptions.put(player.getUniqueID(), player.getName());
        }
        System.out.println("Player subscription = " + playerSubscriptions.get(player.getUniqueID()));
    }

    @Override
    public void onPlayerDisconnect(EntityPlayerMP player) {
        getMeterGroup(player).removePlayer(player.getUniqueID());
    }

    @Override
    public void onCustomPayload(EntityPlayerMP sender, String channel, PacketBuffer data) {
        if ("RSMM".equals(channel)) {
            RSMMSPacket packet = RSMMSPacket.fromBuffer(data);
            if (packet == null) return;
            packet.process(getMeterGroup(sender));
        }
    }

    @Override
    public void onChannelRegister(EntityPlayerMP sender, List<String> channels) {
        if (channels.contains("RSMM")) {
            String groupName = playerSubscriptions.get(sender.getUniqueID());
            if (!meterGroups.containsKey(groupName)) {
                meterGroups.put(groupName, new MeterGroup(this, groupName));
            }
            System.out.println("Adding player to group " + groupName);
            meterGroups.get(groupName).addPlayer(sender.getUniqueID());
        }
    }

    @Override
    public void onChannelUnregister(EntityPlayerMP sender, List<String> channels) {}

    @Override
    public void onStateChange(World world, BlockPos pos) {
        for (MeterGroup mg : meterGroups.values()) {
            mg.onStateChange(world, pos);
        }
    }

    @Override
    public void onTickStart(int tick) {
        for (MeterGroup mg : meterGroups.values()) {
            mg.onTickStart(tick);
        }
    }

    public void subscribePlayerToGroup(EntityPlayerMP player, String groupName) {
        MeterGroup mg = getMeterGroup(player);
        if (mg != null) {
            mg.removePlayer(player.getUniqueID());
        }
        playerSubscriptions.put(player.getUniqueID(), groupName);
        if (!meterGroups.containsKey(groupName)) {
            meterGroups.put(groupName, new MeterGroup(this, groupName));
        }
        meterGroups.get(groupName).addPlayer(player.getUniqueID());
    }

    /* ----- Forward commands to proper meter group ----- */

    public int getNumMeters(EntityPlayerMP player) {
        MeterGroup mg = getMeterGroup(player);
        if (mg == null) {
            return 0;
        } else {
            return mg.getNumMeters();
        }
    }

    public void renameMeter(EntityPlayerMP player, int meterId, String name) {
        MeterGroup mg = getMeterGroup(player);
        if (mg != null) {
            mg.renameMeter(meterId, name);
        }
    }

    public void renameLastMeter(EntityPlayerMP player, String name) {
        MeterGroup mg = getMeterGroup(player);
        if (mg != null) {
            mg.renameLastMeter(name);
        }
    }

    public void recolorMeter(EntityPlayerMP player, int meterId, int color) {
        MeterGroup mg = getMeterGroup(player);
        if (mg != null) {
            mg.recolorMeter(meterId, color);
        }
    }

    public void recolorLastMeter(EntityPlayerMP player, int color) {
        MeterGroup mg = getMeterGroup(player);
        if (mg != null) {
            mg.recolorLastMeter(color);
        }
    }

    public void removeAllMeters(EntityPlayerMP player) {
        MeterGroup mg = getMeterGroup(player);
        if (mg != null) {
            mg.removeAllMeters();
        }
    }
}
