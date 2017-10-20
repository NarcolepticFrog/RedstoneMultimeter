package narcolepticfrog.rsmm;

import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LiteModRedstoneMultimeter implements Tickable, ServerTickable, HUDRenderListener, PostRenderListener, ServerCommandProvider, PistonPushListener {

    private static KeyBinding toggleMeterKey = new KeyBinding("key.redstonemultimeter.toggle", Keyboard.KEY_M, "key.categories.redstonemultimeter");
    private static KeyBinding pauseMetersKey = new KeyBinding("key.redstonemultimeter.pause", Keyboard.KEY_N, "key.categories.redstonemultimeter");
    private static KeyBinding stepForwardKey = new KeyBinding("key.redstonemultimeter.forward", Keyboard.KEY_PERIOD, "key.categories.redstonemultimeter");
    private static KeyBinding stepBackwardKey = new KeyBinding("key.redstonemultimeter.back", Keyboard.KEY_COMMA, "key.categories.redstonemultimeter");

    private MeterManager meterManager = new MeterManager();
    private MeterRenderer renderer = new MeterRenderer(60);
    private boolean metersPaused = false;

    private Lock renderLock = new ReentrantLock();
    private Lock serverLock = new ReentrantLock();

    public LiteModRedstoneMultimeter() {
    }

    public void setWindowLength(int length) {
        renderer.setWindowLength(length);
    }

    public int getNumMeters() {
        return meterManager.getMeters().size();
    }

    public void renameMeter(int ix, String name) {
        List<Meter> meters = meterManager.getMeters();
        if (0 <= ix && ix < meters.size()) {
            meters.get(ix).setName(name);
        }
    }

    public void renameLastMeter(String name) {
        List<Meter> meters = meterManager.getMeters();
        if (meters.size() > 0) {
            meters.get(meters.size() - 1).setName(name);
        }
    }

    public void removeAll() {
        renderLock.lock();
        serverLock.lock();
        try {
            meterManager.removeAll();
        } finally {
            renderLock.unlock();
            serverLock.unlock();
        }
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (toggleMeterKey.isPressed()) {
            RayTraceResult r = minecraft.objectMouseOver;
            if (r.typeOfHit == RayTraceResult.Type.BLOCK) {
                renderLock.lock();
                serverLock.lock();
                try {
                    int dim = minecraft.player.dimension;
                    World world = minecraft.getIntegratedServer().getWorld(dim);
                    // Note: We don't use minecraft.player.world because this is the client's version of the world
                    //       and it can be out of sync with the server's version of the world.
                    meterManager.toggleMeter(r.getBlockPos(), world);
                } finally {
                    serverLock.unlock();
                    renderLock.unlock();
                }
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
        serverLock.lock();
        try {
            for (Meter m : meterManager.getMeters()) {
                m.update(server.getTickCounter());
            }
        } finally {
            serverLock.unlock();
        }
    }

    @Override
    public void onPistonPush(World w, BlockPos pos, EnumFacing direction) {
        serverLock.lock();
        try {
            meterManager.onPistonPush(w, pos, direction);
        } finally {
            serverLock.unlock();
        }
    }

    @Override
    public void onPostRenderHUD(int screenWidth, int screenHeight) {
        renderLock.lock();
        try {
            renderer.renderMeterTraces(meterManager.getMeters(), metersPaused);
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void onPostRender(float partialTicks) {
        renderLock.lock();
        try {
            renderer.renderMeterHighlights(meterManager.getMeters(), partialTicks);
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(toggleMeterKey);
        LiteLoader.getInput().registerKeyBinding(pauseMetersKey);
        LiteLoader.getInput().registerKeyBinding(stepBackwardKey);
        LiteLoader.getInput().registerKeyBinding(stepForwardKey);
        PistonPushEventDispatcher.addListener(this);
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
