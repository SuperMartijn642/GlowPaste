package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.extension.ChunkAccessExtension;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(ImposterProtoChunk.class)
public class ImposterProtoChunkMixin implements ChunkAccessExtension {

    @Final
    @Shadow
    private LevelChunk wrapped;

    @Override
    public @Nullable GlowingBlockStorage.Chunk glowPasteGetGlowingBlockStorage(boolean createIfAbsent){
        return GlowingBlockStorage.get(this.wrapped, createIfAbsent);
    }
}
