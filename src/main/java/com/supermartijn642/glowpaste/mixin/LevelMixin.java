package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.extension.LevelExtension;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(Level.class)
public class LevelMixin implements LevelExtension {

    @Unique
    private GlowingBlockStorage glowingBlockStorage;

    @Override
    public GlowingBlockStorage glowPasteGetGlowingBlockStorage(){
        if(this.glowingBlockStorage == null)
            //noinspection DataFlowIssue
            this.glowingBlockStorage = new GlowingBlockStorage.LevelStorage((Level)(Object)this);
        return this.glowingBlockStorage;
    }
}
