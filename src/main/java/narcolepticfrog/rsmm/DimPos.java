package narcolepticfrog.rsmm;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DimPos {
    private int dim;
    private BlockPos pos;

    public DimPos(World w, BlockPos pos) {
        this(w.provider.getDimensionType().getId(), pos);
    }

    public DimPos(int dim, BlockPos pos) {
        this.dim = dim;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "DimPos[dim = " + dim +
                ", x = " + pos.getX() +
                ", y = " + pos.getY() +
                ", z = " + pos.getZ() + "]";
    }

    public int hashCode() {
        return Integer.hashCode(dim) ^ pos.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DimPos) {
            DimPos o = (DimPos)obj;
            return o.dim == this.dim && o.pos.equals(this.pos);
        }
        return false;
    }
}
