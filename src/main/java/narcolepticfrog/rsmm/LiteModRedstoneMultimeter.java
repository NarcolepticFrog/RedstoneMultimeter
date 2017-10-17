package narcolepticfrog.rsmm;

import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.List;

public class LiteModRedstoneMultimeter implements Tickable, ServerTickable, HUDRenderListener, PostRenderListener, ServerCommandProvider {

    private static KeyBinding toggleMeterKey = new KeyBinding("Toggle Meter", Keyboard.KEY_M, "Redstone Multimeter");
    private static KeyBinding pauseMetersKey = new KeyBinding("Pause Meters", Keyboard.KEY_N, "Redstone Multimeter");

    private MeterManager meterManager = new MeterManager();
    private int meterDuration = 60;
    private boolean metersPaused = false;

    public LiteModRedstoneMultimeter() {
    }

    public void setDuration(int duration) {
        this.meterDuration = duration;
        for (Meter m : meterManager.getMeters()) {
            m.setDuration(duration);
        }
    }

    public void renameMeter(int ix, String name) {
        List<Meter> meters = meterManager.getMeters();
        if (meters.size() > ix) {
            meters.get(ix).setName(name);
        }
    }

    public void renameLastMeter(String name) {
        List<Meter> meters = meterManager.getMeters();
        if (meters.size() > 0) {
            meters.get(meters.size()-1).setName(name);
        }
    }

    public void removeAll() {
        meterManager.removeAll();
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (toggleMeterKey.isPressed()) {
            RayTraceResult r = minecraft.objectMouseOver;
            if (r.typeOfHit == RayTraceResult.Type.BLOCK) {
                meterManager.toggleMeter(r.getBlockPos(), minecraft.player.dimension, meterDuration);
            }
        }
        if (pauseMetersKey.isPressed()) {
            metersPaused = !metersPaused;
        }
    }

    @Override
    public void onTick(MinecraftServer server) {
        if (!metersPaused) {
            for (Meter m : meterManager.getMeters()) {
                for (int dim = 0; dim < server.worlds.length; dim++) {
                    m.update(server.worlds[dim], dim);
                }
            }
        }
    }


    @Override
    public void onPostRenderHUD(int screenWidth, int screenHeight) {
        MeterRendering.renderMeterTraces(meterManager.getMeters(), metersPaused);
    }

    @Override
    public void onPostRender(float partialTicks) {
        MeterRendering.renderMeterHighlights(meterManager.getMeters(), partialTicks);
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(toggleMeterKey);
        LiteLoader.getInput().registerKeyBinding(pauseMetersKey);
    }

    @Override
    public String getName() {
        return "Redstone Multimeter";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }


    @Override
    public void provideCommands(ServerCommandManager commandManager) {
        commandManager.registerCommand(new MeterCommand(this));
    }

    /* ----- Unused Interface Methods ----- */

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public void onPostRenderEntities(float partialTicks) {
    }

    @Override
    public void onPreRenderHUD(int screenWidth, int screenHeight) {
    }


}
