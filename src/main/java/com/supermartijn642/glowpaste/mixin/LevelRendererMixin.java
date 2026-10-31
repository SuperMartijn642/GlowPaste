package com.supermartijn642.glowpaste.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.supermartijn642.glowpaste.content.GlowingBlockHighlighter;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    private ClientLevel level;
    @Shadow
    private int lastViewDistance;
    @Final
    @Shadow
    private LevelRenderState levelRenderState;
    @Final
    @Shadow
    private SubmitNodeStorage submitNodeStorage;

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;extractVisibleEntities(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;Lnet/minecraft/client/DeltaTracker;Lnet/minecraft/client/renderer/state/LevelRenderState;)V"
        )
    )
    private void extractLevel(GraphicsResourceAllocator graphicsAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f cameraRotationMatrix, Matrix4f projectionMatrix, Matrix4f otherProjectionMatrix, GpuBufferSlice gpuBufferSlice, Vector4f fogColor, boolean renderSky, CallbackInfo ci){
        GlowingBlockHighlighter.extractHighlights(this.level, this.levelRenderState, camera, this.lastViewDistance);
    }

    @Inject(
        method = "lambda$addMainPass$1(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;ZLnet/minecraft/client/renderer/culling/Frustum;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            shift = At.Shift.BEFORE
        )
    )
    private void submitGlowingBlockHighlights(GpuBufferSlice bufferSlice, LevelRenderState levelRenderState, ProfilerFiller profiler, Matrix4f modelViewMatrix, ResourceHandle<RenderTarget> mainTarget, ResourceHandle<RenderTarget> translucentTarget, boolean renderBlockOutline, Frustum frustum, ResourceHandle<RenderTarget> itemEntityTarget, ResourceHandle<RenderTarget> entityOutlineTarget, CallbackInfo ci){
        GlowingBlockHighlighter.submitHighlights(levelRenderState, this.submitNodeStorage);
    }
}
