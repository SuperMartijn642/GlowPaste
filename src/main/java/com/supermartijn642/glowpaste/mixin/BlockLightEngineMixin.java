package com.supermartijn642.glowpaste.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.extension.ChunkAccessExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@SuppressWarnings("rawtypes")
@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin extends LightEngine {

    @Unique
    private final BlockPos.MutableBlockPos dummyPos = new BlockPos.MutableBlockPos();

    private BlockLightEngineMixin(){
        //noinspection DataFlowIssue,unchecked
        super(null, null);
    }

    @Inject(
        method = "getEmission",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I",
            shift = At.Shift.AFTER
        )
    )
    private void checkGlowingBlockEmission(long blockNode, BlockState state, CallbackInfoReturnable<Integer> ci, @Local LocalIntRef lightEmission) {
        if(lightEmission.get() >= 15)
            return;
        this.dummyPos.set(blockNode);
        LightChunk chunk = this.chunkSource.getChunkForLighting(this.dummyPos.getX() >> 4, this.dummyPos.getZ() >> 4);
        if(!(chunk instanceof ChunkAccess))
            return;
        GlowingBlockStorage.Chunk storage = GlowingBlockStorage.get((ChunkAccess)chunk, false);
        if(storage == null)
            return;
        if(storage.has(this.dummyPos.getX() % 16, this.dummyPos.getY(), this.dummyPos.getZ() % 16))
            lightEmission.set(15);
    }

    @Inject(
        method = "propagateLightSources",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/chunk/LightChunk;findBlockLightSources(Ljava/util/function/BiConsumer;)V",
            shift = At.Shift.BEFORE
        )
    )
    private void addGlowingBlocksLightSources(ChunkPos pos, CallbackInfo ci, @Local LightChunk chunk){
        if(!(chunk instanceof ChunkAccess))
            return;
        GlowingBlockStorage.Chunk storage = GlowingBlockStorage.get((ChunkAccess)chunk, false);
        if(storage == null)
            return;
        storage.forEach((x, y, z) -> {
            this.dummyPos.set(x, y, z);
            BlockState state = chunk.getBlockState(this.dummyPos);
            this.enqueueIncrease(this.dummyPos.asLong(), LightEngine.QueueEntry.increaseLightFromEmission(15, isEmptyShape(state)));
        });
    }
}
