package com.supermartijn642.glowpaste.generators;

import com.supermartijn642.core.generator.ItemInfoGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.glowpaste.GlowPaste;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public class GlowPasteItemInfoGenerator extends ItemInfoGenerator {

    public GlowPasteItemInfoGenerator(String modid, ResourceCache cache){
        super(modid, cache);
    }

    @Override
    public void generate(){
        this.simpleInfo(GlowPaste.glowPaste, "item/glow_paste");
    }
}
