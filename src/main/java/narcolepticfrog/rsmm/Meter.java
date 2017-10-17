package narcolepticfrog.rsmm;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class Meter {

    private String name;
    private int color;
    private Trace<Boolean> trace;
    private BlockPos position;
    private int dimension;

    public Meter(BlockPos position, int dimension, String name, int duration, int color) {
        this.position = position;
        this.dimension = dimension;
        this.name = name;
        this.trace = new Trace<Boolean>(duration);
        this.color = color;
    }

    public void update(WorldServer world, int dimension) {
        if (dimension == this.dimension) {
            IBlockState state = world.getBlockState(position);
            Meterable m = (Meterable)state.getBlock();
            trace.push(m.isPowered(state, world, position));
        }
    }

    public void setDuration(int ticks) {
        this.trace = this.trace.copyWithCapacity(ticks);
    }

    public int getDuration() {
        return this.trace.capacity();
    }

    public Trace<Boolean> getTrace() {
        return trace;
    }

    public int getColor() {
        return color;
    }

    public BlockPos getPosition() {
        return position;
    }

    public int getDimension() {
        return dimension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void clear() {
        trace.clear();
    }

}
