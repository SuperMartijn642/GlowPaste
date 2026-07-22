package com.supermartijn642.glowpaste.extension;

import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public interface ChunkAccessExtension {

    @Nullable
    @Contract("true->!null")
    GlowingBlockStorage.Chunk glowPasteGetGlowingBlockStorage(boolean createIfAbsent);
}
