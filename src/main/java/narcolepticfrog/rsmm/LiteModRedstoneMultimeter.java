package narcolepticfrog.rsmm;

import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.client.ClientPluginChannelsClient;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.PluginChannels;
import narcolepticfrog.rsmm.clock.SubtickClock;
import narcolepticfrog.rsmm.clock.SubtickTime;
import narcolepticfrog.rsmm.network.*;
import narcolepticfrog.rsmm.server.RSMMServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LiteModRedstoneMultimeter implements Tickable, HUDRenderListener, PostRenderListener, PreRenderListener,
        ServerCommandProvider, PluginChannelListener, RSMMCPacketHandler, PreJoinGameListener {

    private static KeyBinding toggleMeterKey = new KeyBinding("key.redstonemultimeter.toggle", Keyboard.KEY_M, "key.categories.redstonemultimeter");
    private static KeyBinding pauseMetersKey = new KeyBinding("key.redstonemultimeter.pause", Keyboard.KEY_N, "key.categories.redstonemultimeter");
    private static KeyBinding stepForwardKey = new KeyBinding("key.redstonemultimeter.forward", Keyboard.KEY_PERIOD, "key.categories.redstonemultimeter");
    private static KeyBinding stepBackwardKey = new KeyBinding("key.redstonemultimeter.back", Keyboard.KEY_COMMA, "key.categories.redstonemultimeter");
    private static KeyBinding toggleOverlayKey = new KeyBinding("key.redstonemultimeter.toggledisplay", Keyboard.KEY_H, "key.categories.redstonemultimeter");

    private SubtickClock clock = new SubtickClock();
    private MeterRenderer renderer = new MeterRenderer(clock, 60);
    private RSMMServer rsmmServer = new RSMMServer();
    private boolean metersPaused = false;
    private boolean renderMeterOverlay = true;
    private ArrayList<Meter> meters = new ArrayList<>();
    private long currentTick;

    public LiteModRedstoneMultimeter() {
    }

    public void setWindowLength(int length) {
        renderer.setWindowLength(length);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (minecraft.world != null) {
            currentTick = minecraft.world.getTotalWorldTime();
        }
        if (toggleMeterKey.isPressed()) {
            RayTraceResult r = minecraft.objectMouseOver;
            if (r.typeOfHit == RayTraceResult.Type.BLOCK) {
                int dim = minecraft.player.dimension;
                boolean movable = !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
                DimPos dimpos = new DimPos(dim, r.getBlockPos());
                RSMMSPacketToggleMeter packet = new RSMMSPacketToggleMeter(dimpos, movable);
                ClientPluginChannelsClient.sendMessage("RSMM", packet.toBuffer(), PluginChannels.ChannelPolicy.DISPATCH_IF_REGISTERED);
            }
        }
        if (pauseMetersKey.isPressed()) {
            metersPaused = !metersPaused;
        }
        if (toggleOverlayKey.isPressed()) {
            renderMeterOverlay = !renderMeterOverlay;
        }

        if (metersPaused) {
            boolean ctrlPressed = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            int dist = ctrlPressed ? 10 : 1;
            if (stepForwardKey.isPressed()) {
                renderer.setWindowStartTick(renderer.getWindowStartTick() + dist);
            }
            if (stepBackwardKey.isPressed()) {
                renderer.setWindowStartTick(renderer.getWindowStartTick() - dist);
            }
        }
    }

    @Override
    public void onPostRenderHUD(int screenWidth, int screenHeight) {
        if (!metersPaused) {
            long delta = currentTick - renderer.getWindowStartTick();
            long windowStartTick = (int)(renderer.getWindowStartTick() + 0.3*delta) + 1;
            windowStartTick = Math.min(windowStartTick, currentTick);
            renderer.setWindowStartTick(windowStartTick);
        }
        if (renderMeterOverlay) {
            renderer.renderMeterTraces(meters, metersPaused);
        }
    }

    @Override
    public void onPostRenderEntities(float partialTicks) {
        renderer.renderMeterHighlights(meters, partialTicks);
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(toggleMeterKey);
        LiteLoader.getInput().registerKeyBinding(pauseMetersKey);
        LiteLoader.getInput().registerKeyBinding(stepBackwardKey);
        LiteLoader.getInput().registerKeyBinding(stepForwardKey);
        LiteLoader.getInput().registerKeyBinding(toggleOverlayKey);
    }

    @Override
    public String getName() {
        return "Redstone Multimeter";
    }

    @Override
    public String getVersion() {
        return "0.5";
    }

    @Override
    public void provideCommands(ServerCommandManager commandManager) {
        commandManager.registerCommand(new MeterCommand(rsmmServer));
    }

    @Override
    public boolean onPreJoinGame(INetHandler netHandler, SPacketJoinGame joinGamePacket) {
        meters.clear();
        clock.clear();
        return true;
    }

    @Override
    public void handleMeter(RSMMCPacketMeter packet) {
        if (packet.shouldCreate()) {
            Meter m = new Meter(clock, packet.getDimpos(), packet.getName(), packet.getColor(), packet.isMovable());
            m.registerStateChange(new SubtickTime(clock.getTick()), packet.isPowered());
            meters.add(m);
        } else if (packet.shouldDelete()) {
            meters.remove(packet.getMeterId());
        } else {
            int meterId = packet.getMeterId();
            Meter m = meters.get(meterId);
            if (packet.hasName()) m.setName(packet.getName());
            if (packet.hasColor()) m.setColor(packet.getColor());
            if (packet.hasDimPos()) m.registerMove(packet.getTime(), packet.getDimpos());
            if (packet.hasPowered()) m.registerStateChange(packet.getTime(), packet.isPowered());
            clock.registerTime(packet.getTime());
            // Temporary. Once rendering is improved, I will remove this. TODO: Remove
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(packet.getTime().getPhase().toString()));
        }
    }

    @Override
    public void handleMeterGroup(RSMMCPacketMeterGroup packet) {
        meters.clear();
        clock.clear();
        renderer.setGroupName(packet.getGroupName());
    }

    @Override
    public void onCustomPayload(String channel, PacketBuffer data) {
        if ("RSMM".equals(channel)) {
            RSMMCPacket packet = RSMMCPacket.fromBuffer(data);
            packet.process(this);
        }
    }

    @Override
    public List<String> getChannels() {
        return ImmutableList.of("RSMM");
    }

    /* ----- Unused Interface Methods ----- */

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

    @Override
    public void onPostRender(float partialTicks) { }

    @Override
    public void onPreRenderHUD(int screenWidth, int screenHeight) {}

    @Override
    public void onRenderWorld(float partialTicks) { }

    @Override
    public void onSetupCameraTransform(float partialTicks, int pass, long timeSlice) {}

    @Override
    public void onRenderSky(float partialTicks, int pass) {}

    @Override
    public void onRenderClouds(float partialTicks, int pass, RenderGlobal renderGlobal) {}

    @Override
    public void onRenderTerrain(float partialTicks, int pass) { }

}
