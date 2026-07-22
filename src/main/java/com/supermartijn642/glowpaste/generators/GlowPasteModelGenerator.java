package com.supermartijn642.glowpaste.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.GlowPasteClient;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public class GlowPasteModelGenerator extends ModelGenerator {

    public GlowPasteModelGenerator(String modid, ResourceCache cache){
        super(modid, cache);
    }

    @Override
    public void generate(){
        // Item model
        this.itemGenerated("item/glow_paste", GlowPaste.identifier("glow_paste"));

        // Models for highlighting glowing blocks
        this.model(GlowPasteClient.HIGHLIGHT_MODEL_LOCATION)
            .texture("all", "glowing_boundary")
            .particleTexture("#all")
            .element(element ->
                element.from(-0.5f, -0.5f, -0.5f)
                    .to(16.5f, 16.5f, 16.5f)
                    .allFaces(face -> face.texture("#all").uv(0,0,16,16))
            );
        this.model(GlowPasteClient.SMALL_HIGHLIGHT_MODEL_LOCATION)
            .texture("all", "glowing_boundary_small")
            .particleTexture("#all")
            .element(element ->
                element.from(1.5f, 1.5f, 1.5f)
                    .to(14.5f, 14.5f, 14.5f)
                    .allFaces(face -> face.texture("#all").uv(2,2,14,14))
            );
    }
}
