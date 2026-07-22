package com.supermartijn642.glowpaste.generators;

import com.supermartijn642.fusion.api.provider.FusionTextureMetadataProvider;
import com.supermartijn642.fusion.api.texture.DefaultTextureTypes;
import com.supermartijn642.fusion.api.texture.types.base.BaseTextureData;
import com.supermartijn642.glowpaste.GlowPaste;
import net.minecraft.data.PackOutput;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
public class GlowPasteFusionTextureMetadataProvider extends FusionTextureMetadataProvider {

    public GlowPasteFusionTextureMetadataProvider(String modid, PackOutput output){
        super(modid, output);
    }

    @Override
    protected void generate(){
        this.addTextureMetadata(GlowPaste.identifier("glow_paste"), DefaultTextureTypes.BASE, BaseTextureData.builder().emissive(true).build());
    }
}
