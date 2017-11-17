package narcolepticfrog.rsmm;

import com.mumfrey.liteloader.HUDRenderListener;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.ServerCommandProvider;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import narcolepticfrog.rsmm.clock.SubtickClock;
import narcolepticfrog.rsmm.events.PistonPushEventDispatcher;
import narcolepticfrog.rsmm.events.PistonPushListener;
import narcolepticfrog.rsmm.events.StateChangeEventDispatcher;
import narcolepticfrog.rsmm.events.StateChangeListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LiteModRedstoneMultimeter implements Tickable, HUDRenderListener, PostRenderListener,
        ServerCommandProvider, PistonPushListener, StateChangeListener {

    private static KeyBinding toggleMeterKey = new KeyBinding("key.redstonemultimeter.toggle", Keyboard.KEY_M, "key.categories.redstonemultimeter");
    private static KeyBinding pauseMetersKey = new KeyBinding("key.redstonemultimeter.pause", Keyboard.KEY_N, "key.categories.redstonemultimeter");
    private static KeyBinding stepForwardKey = new KeyBinding("key.redstonemultimeter.forward", Keyboard.KEY_PERIOD, "key.categories.redstonemultimeter");
    private static KeyBinding stepBackwardKey = new KeyBinding("key.redstonemultimeter.back", Keyboard.KEY_COMMA, "key.categories.redstonemultimeter");

    private MeterManager meterManager = new MeterManager();
    private MeterRenderer renderer = new MeterRenderer(60);
    private boolean metersPaused = false;

    private Lock mutex = new ReentrantLock();

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
        mutex.lock();
        try {
            meterManager.removeAll();
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (toggleMeterKey.isPressed()) {
            RayTraceResult r = minecraft.objectMouseOver;
            if (r.typeOfHit == RayTraceResult.Type.BLOCK) {
                mutex.lock();
                try {
                    int dim = minecraft.player.dimension;
                    World world = minecraft.getIntegratedServer().getWorld(dim);
                    // Note: We don't use minecraft.player.world because this is the client's version of the world
                    //       and it can be out of sync with the server's version of the world.
                    meterManager.toggleMeter(r.getBlockPos(), world);
                } finally {
                    mutex.unlock();
                }
            }
        }
        if (pauseMetersKey.isPressed()) {
            metersPaused = !metersPaused;
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
    public void onPistonPush(World w, BlockPos pos, EnumFacing direction) {
        mutex.lock();
        try {
            meterManager.onPistonPush(w, pos, direction);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void onStateChange(World world, BlockPos pos) {
        mutex.lock();
        try {
            Meter m = meterManager.getMeter(world, pos);
            if (m != null) {
                m.checkForUpdate();
            }
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void onPostRenderHUD(int screenWidth, int screenHeight) {
        mutex.lock();
        try {
            if (!metersPaused) {
                int currentTick = SubtickClock.getClock().getTick();
                int delta = currentTick - renderer.getWindowStartTick();
                int windowStartTick = (int)(renderer.getWindowStartTick() + 0.3*delta) + 1;
                windowStartTick = Math.min(windowStartTick, currentTick);
                renderer.setWindowStartTick(windowStartTick);
            }
            renderer.renderMeterTraces(meterManager.getMeters(), metersPaused);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void onPostRender(float partialTicks) {
        mutex.lock();
        try {
            renderer.renderMeterHighlights(meterManager.getMeters(), partialTicks);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(toggleMeterKey);
        LiteLoader.getInput().registerKeyBinding(pauseMetersKey);
        LiteLoader.getInput().registerKeyBinding(stepBackwardKey);
        LiteLoader.getInput().registerKeyBinding(stepForwardKey);
        PistonPushEventDispatcher.addListener(this);
        StateChangeEventDispatcher.addListener(this);
    }

    @Override
    public String getName() {
        return "Redstone Multimeter";
    }

    @Override
    public String getVersion() {
        return "0.2-pre";
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
