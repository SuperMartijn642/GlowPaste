package com.supermartijn642.glowpaste.generators;

import com.supermartijn642.core.generator.LanguageGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.glowpaste.GlowPaste;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public class GlowPasteLanguageGenerator extends LanguageGenerator {

    public GlowPasteLanguageGenerator(String modid, ResourceCache cache){
        super(modid, cache, "en_us");
    }

    @Override
    public void generate(){
        this.item(GlowPaste.glowPaste, "Glow Paste");
        this.translation("glowpaste.glow_paste.description", "Use on a block to make it emit light");
    }
}
