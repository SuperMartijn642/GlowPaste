package com.supermartijn642.glowpaste.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.supermartijn642.glowpaste.content.GlowingBlockHighlighter;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
        method = "extractLevel",
        at = @At("HEAD")
    )
    private void extractLevel(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        GlowingBlockHighlighter.extractHighlights(this.level, this.levelRenderState, camera, this.lastViewDistance);
    }

    @Inject(
        method = "lambda$addMainPass$0(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;ZLorg/joml/Matrix4fc;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            shift = At.Shift.BEFORE
        )
    )
    private void submitGlowingBlockHighlights(GpuBufferSlice bufferSlice, LevelRenderState levelRenderState, ProfilerFiller profiler, ChunkSectionsToRender chunksToRender, ResourceHandle<RenderTarget> mainTarget, ResourceHandle<RenderTarget> translucentTarget, ResourceHandle<RenderTarget> itemEntityTarget, ResourceHandle<RenderTarget> entityOutlineTarget, ResourceHandle<RenderTarget> particleTarget, boolean renderOutline, Matrix4fc modelViewMatrix, CallbackInfo ci) {
        GlowingBlockHighlighter.submitHighlights(levelRenderState, this.submitNodeStorage);
    }
}
