package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.extension.ChunkAccessExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(SerializableChunkData.class)
public class SerializableChunkDataMixin {

    @Unique
    private static final String DATA_KEY = GlowPaste.identifier("glowing_blocks").toString();

    @Unique
    private ListTag storageData;

    @Inject(
        method = "write",
        at = @At("RETURN")
    )
    private void writeStorage(CallbackInfoReturnable<CompoundTag> ci){
        CompoundTag tag = ci.getReturnValue();
        if(tag == null)
            return;
        if(this.storageData != null)
            tag.put(DATA_KEY, this.storageData);
    }

    @Inject(
        method = "parse",
        at = @At("RETURN")
    )
    private static void readStorage(LevelHeightAccessor levelHeight, PalettedContainerFactory containerFactory, CompoundTag chunkData, CallbackInfoReturnable<SerializableChunkData> ci){
        SerializableChunkData serializableChunkData = ci.getReturnValue();
        if(serializableChunkData == null)
            return;
        //noinspection DataFlowIssue
        ((SerializableChunkDataMixin)(Object)serializableChunkData).storageData = chunkData.getList(DATA_KEY).orElse(null);
    }

    @Inject(
        method = "copyOf",
        at = @At("RETURN")
    )
    private static void serializeStorage(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<SerializableChunkData> ci){
        SerializableChunkData serializableChunkData = ci.getReturnValue();
        if(serializableChunkData == null)
            return;
        GlowingBlockStorage.Chunk storage = GlowingBlockStorage.get(chunk, false);
        if(storage == null)
            return;
        try{
            //noinspection DataFlowIssue
            ((SerializableChunkDataMixin)(Object)serializableChunkData).storageData = storage.serialize();
        }catch(Exception e){
            GlowPaste.LOGGER.error("Failed to serialize glowing blocks chunk data!", e);
        }
    }

    @Inject(
        method = "read",
        at = @At("RETURN")
    )
    private void deserializeStorage(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionInfo, ChunkPos pos, CallbackInfoReturnable<ProtoChunk> ci){
        ProtoChunk protoChunk = ci.getReturnValue();
        if(protoChunk == null)
            return;
        if(this.storageData == null)
            return;
        try{
            GlowingBlockStorage.get(protoChunk, true).deserialize(this.storageData);
        }catch(Exception e){
            GlowPaste.LOGGER.error("Failed to deserialize glowing blocks chunk data!", e);
        }
    }
}
