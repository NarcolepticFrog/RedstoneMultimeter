package narcolepticfrog.rsmm;

import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
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
    private static KeyBinding stepForwardKey = new KeyBinding("Step Forward", Keyboard.KEY_PERIOD,
            "Redstone Multimeter");
    private static KeyBinding stepBackwardKey = new KeyBinding("Step Backward", Keyboard.KEY_COMMA,
            "Redstone Multimeter");

    private MeterManager meterManager = new MeterManager();
    private MeterRenderer renderer = new MeterRenderer(60);
    private boolean metersPaused = false;

    public LiteModRedstoneMultimeter() {
    }

    public void setWindowLength(int length) {
        renderer.setWindowLength(length);
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
                meterManager.toggleMeter(r.getBlockPos(), minecraft.player.dimension, 1000);
            }
        }
        if (pauseMetersKey.isPressed()) {
            metersPaused = !metersPaused;
        }

        if (metersPaused) {
            if (stepForwardKey.isPressed()) {
                renderer.setWindowStartTick(renderer.getWindowStartTick() + 10);
            }
            if (stepBackwardKey.isPressed()) {
                renderer.setWindowStartTick(renderer.getWindowStartTick() - 10);
            }
        }
    }

    @Override
    public void onTick(MinecraftServer server) {
        if (!metersPaused) {
            int delta = server.getTickCounter() - renderer.getWindowStartTick();
            int windowStartTick = (int)(renderer.getWindowStartTick() + 0.3*delta) + 1;
            if (windowStartTick > server.getTickCounter()) {
                windowStartTick = server.getTickCounter();
            }
            renderer.setWindowStartTick(windowStartTick);
        }
        for (Meter m : meterManager.getMeters()) {
            for (int dim = 0; dim < server.worlds.length; dim++) {
                m.update(server.getTickCounter(), server.worlds[dim], dim);
            }
        }
    }


    @Override
    public void onPostRenderHUD(int screenWidth, int screenHeight) {
        renderer.renderMeterTraces(meterManager.getMeters(), metersPaused);
    }

    @Override
    public void onPostRender(float partialTicks) {
        renderer.renderMeterHighlights(meterManager.getMeters(), partialTicks);
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(toggleMeterKey);
        LiteLoader.getInput().registerKeyBinding(pauseMetersKey);
        LiteLoader.getInput().registerKeyBinding(stepBackwardKey);
        LiteLoader.getInput().registerKeyBinding(stepForwardKey);
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
