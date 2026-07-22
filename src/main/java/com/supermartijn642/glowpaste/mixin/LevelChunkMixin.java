package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.extension.ChunkAccessExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(LevelChunk.class)
public class LevelChunkMixin extends ChunkAccessMixin {

    @Final
    @Shadow
    private Level level;

    @Inject(
        method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V",
        at = @At("TAIL")
    )
    private void copyStorage(ServerLevel level, ProtoChunk protoChunk, LevelChunk.PostLoadProcessor postLoadProcessor, CallbackInfo ci){
        this.storage = GlowingBlockStorage.get(protoChunk, false);
    }

    @Inject(
        method = "setBlockState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;",
            shift = At.Shift.AFTER
        )
    )
    private void setBlockState(BlockPos pos, BlockState state, @Block.UpdateFlags int flags, CallbackInfoReturnable<BlockState> ci) {
        if(!state.isAir())
            return;
        GlowingBlockStorage.Chunk storage = this.glowPasteGetGlowingBlockStorage(false);
        if(storage == null || !storage.has(pos.getX() % 16, pos.getY(), pos.getZ() % 16))
            return;
        GlowingBlockStorage.get(this.level).clear(pos.getX(), pos.getY(), pos.getZ());
    }
}
