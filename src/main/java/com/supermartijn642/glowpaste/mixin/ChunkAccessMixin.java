package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.extension.ChunkAccessExtension;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements ChunkAccessExtension {

    @Final
    @Shadow
    private LevelChunkSection[] sections;

    @Unique
    protected GlowingBlockStorage.Chunk storage;

    @Override
    public @Nullable GlowingBlockStorage.Chunk glowPasteGetGlowingBlockStorage(boolean createIfAbsent){
        if(createIfAbsent && this.storage == null)
            //noinspection DataFlowIssue
            this.storage = new GlowingBlockStorage.Chunk((ChunkAccess)(Object)this, this.sections.length);
        return this.storage;
    }
}
