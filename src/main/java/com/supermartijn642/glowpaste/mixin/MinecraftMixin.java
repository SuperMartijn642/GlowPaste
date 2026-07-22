package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.content.GlowingBlockHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
        method = "updateLevelInEngines",
        at = @At("TAIL")
    )
    private void updateLevelInEngines(ClientLevel level, boolean stopSound, CallbackInfo ci){
        GlowingBlockHighlighter.clear();
    }
}
