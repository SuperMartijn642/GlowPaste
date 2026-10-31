package com.supermartijn642.glowpaste.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.glowpaste.content.GlowingBlockHighlighter;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    private ClientLevel level;
    @Shadow
    private int lastViewDistance;

    @Inject(
        method = "renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)Z",
        at = @At("HEAD")
    )
    private void submitGlowingBlockHighlights(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, MultiBufferSource.BufferSource overlayBufferSource, Camera camera, float partialTicks, Frustum frustum, CallbackInfoReturnable<Boolean> ci){
        GlowingBlockHighlighter.extractHighlights(this.level, camera, this.lastViewDistance);
        GlowingBlockHighlighter.renderHighlights(bufferSource, camera);
    }
}
