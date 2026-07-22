package com.supermartijn642.glowpaste.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.glowpaste.GlowPaste;
import net.minecraftforge.common.Tags;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
public class GlowPasteRecipeGenerator extends RecipeGenerator {

    public GlowPasteRecipeGenerator(String modid, ResourceCache cache){
        super(modid, cache);
    }

    @Override
    public void generate(){
        this.shaped(GlowPaste.glowPaste, 8)
            .pattern("GGG")
            .pattern("GSG")
            .pattern("GGG")
            .input('G', Tags.Items.DUSTS_GLOWSTONE)
            .input('S', Tags.Items.SLIME_BALLS)
            .unlockedBy(Tags.Items.DUSTS_GLOWSTONE);
    }
}
