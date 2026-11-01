package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {

    @Unique
    private static final String DATA_KEY = GlowPaste.identifier("glowing_blocks").toString();

    @Inject(
        method = "write",
        at = @At("RETURN")
    )
    private static void writeStorage(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> ci){
        CompoundTag tag = ci.getReturnValue();
        if(tag == null)
            return;
        GlowingBlockStorage.Chunk storage = GlowingBlockStorage.get(chunk, false);
        if(storage == null)
            return;
        ListTag storageData = null;
        try{
            storageData = storage.serialize();
        }catch(Exception e){
            GlowPaste.LOGGER.error("Failed to serialize glowing blocks chunk data!", e);
        }
        if(storageData != null)
            tag.put(DATA_KEY, storageData);
    }

    @Inject(
        method = "read",
        at = @At("RETURN")
    )
    private static void readStorage(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos chunkPos, CompoundTag chunkData, CallbackInfoReturnable<ProtoChunk> ci){
        ProtoChunk protoChunk = ci.getReturnValue();
        if(protoChunk == null)
            return;
        ListTag storageData = chunkData.getList(DATA_KEY, Tag.TAG_COMPOUND);
        if(storageData.isEmpty())
            return;
        try{
            GlowingBlockStorage.get(protoChunk, true).deserialize(storageData);
        }catch(Exception e){
            GlowPaste.LOGGER.error("Failed to deserialize glowing blocks chunk data!", e);
        }
    }
}
