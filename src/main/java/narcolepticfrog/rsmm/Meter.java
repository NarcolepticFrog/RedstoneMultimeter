package narcolepticfrog.rsmm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Meter {

    public class PowerInterval {

        private int startTick;
        private int endTick;
        private boolean powered;

        public PowerInterval(int startTick, int endTick, boolean isPowered) {
            setStartTick(startTick);
            setEndTick(endTick);
            setPowered(isPowered);
        }

        public PowerInterval(int startTick, boolean isPowered) {
            this(startTick, startTick, isPowered);
        }

        public void setStartTick(int startTick) {
            this.startTick = startTick;
        }

        public int getStartTick() {
            return this.startTick;
        }

        public void setEndTick(int endTick) {
            this.endTick = endTick;
        }

        public int getEndTick() {
            return this.endTick;
        }

        public boolean isPowered() {
            return powered;
        }

        public void setPowered(boolean powered) {
            this.powered = powered;
        }

    }

    private String name;
    private int color;
    private Trace<PowerInterval> trace;
    private BlockPos position;
    private int dimension;

    public Meter(BlockPos position, int dimension, String name, int maxIntervals, int color) {
        this.position = position;
        this.dimension = dimension;
        this.name = name;
        this.trace = new Trace<PowerInterval>(maxIntervals);
        this.color = color;
    }

    public void update(int currentTick, World world, int dimension) {
        if (dimension == this.dimension) {
            IBlockState state = world.getBlockState(position);
            Meterable m = (Meterable) state.getBlock();
            boolean isPowered = m.isPowered(state, world, position);

            if ((trace.size() == 0 && isPowered) || (trace.size() > 0 && trace.get(0).isPowered() != isPowered)) {
                trace.push(new PowerInterval(currentTick, isPowered));
            } else if (trace.size() > 0) {
                trace.get(0).setEndTick(currentTick);
            }
        }
    }

    public Trace<PowerInterval> getPowerIntervals() {
        return trace;
    }

    public void setMaxIntervals(int ticks) {
        this.trace = this.trace.copyWithCapacity(ticks);
    }

    public int getMaxIntervals() {
        return this.trace.capacity();
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
