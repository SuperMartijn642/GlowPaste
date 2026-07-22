package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.extension.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(BlockGetter.class)
public interface BlockGetterMixin {

    @Inject(
        method = "getLightEmission",
        at = @At("HEAD")
    )
    private void checkGlowingBlockEmission(BlockPos pos, CallbackInfoReturnable<Integer> ci) {
        if(!(this instanceof LevelExtension))
            return;
        if(((LevelExtension)this).glowPasteGetGlowingBlockStorage().has(pos.getX(), pos.getY(), pos.getZ()))
            ci.setReturnValue(15);
    }
}
