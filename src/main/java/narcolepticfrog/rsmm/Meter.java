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

    public boolean isPowered(WorldServer world, BlockPos position) {
        Block block = world.getBlockState(position).getBlock();
        IBlockState blockState = world.getBlockState(position);
        if (block instanceof BlockRedstoneRepeater) {
            return block == Blocks.POWERED_REPEATER;
        } else if (block instanceof BlockRedstoneComparator) {
            TileEntityComparator tec = (TileEntityComparator) world.getTileEntity(position);
            return tec.getOutputSignal() > 0;
        } else if (block instanceof BlockRedstoneTorch) {
            return block == Blocks.REDSTONE_TORCH;
        } else if (block instanceof BlockObserver) {
            return blockState.getValue(BlockObserver.POWERED);
        } else if (block instanceof BlockLever) {
            return blockState.getValue(BlockLever.POWERED);
        } else if (block instanceof BlockButton) {
            return blockState.getValue(BlockButton.POWERED);
        } else if (block instanceof BlockPressurePlate) {
            return blockState.getValue(BlockPressurePlate.POWERED);
        } else if (block instanceof BlockPressurePlateWeighted) {
            return blockState.getValue(BlockPressurePlateWeighted.POWER) > 0;
        } else if (block instanceof BlockTripWireHook) {
            return blockState.getValue(BlockTripWireHook.POWERED);
        } else {
            return world.isBlockPowered(position);
        }
    }

    public void update(WorldServer world, int dimension) {
        if (dimension == this.dimension) {
            trace.push(isPowered(world, position));
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
