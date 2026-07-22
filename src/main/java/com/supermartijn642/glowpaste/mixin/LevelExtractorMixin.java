package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.content.GlowingBlockHighlighter;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
@Mixin(LevelExtractor.class)
public class LevelExtractorMixin {

    @Shadow
    private ClientLevel level;
    @Final
    @Shadow
    private LevelRenderState levelRenderState;
    @Shadow
    private int lastViewDistance;

    @Inject(
        method = "extract",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getGameTime()J"
        )
    )
    private void extract(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        GlowingBlockHighlighter.extractHighlights(this.level, this.levelRenderState, camera, this.lastViewDistance);
    }
}
