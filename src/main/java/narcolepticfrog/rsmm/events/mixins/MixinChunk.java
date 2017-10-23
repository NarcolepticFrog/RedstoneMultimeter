package narcolepticfrog.rsmm.events.mixins;

import narcolepticfrog.rsmm.events.StateChangeEventDispatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class)
public class MixinChunk {

    @Shadow
    private World world;

    @Inject(method = "setBlockState", at = @At("RETURN"))
    public void onSetBlockState(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> ci) {
        if (!world.isRemote) {
            StateChangeEventDispatcher.dispatchEvent(world, pos);
        }
    }

}
