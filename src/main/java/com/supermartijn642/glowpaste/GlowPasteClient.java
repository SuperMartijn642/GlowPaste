package com.supermartijn642.glowpaste;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod(value = GlowPaste.MODID, dist = Dist.CLIENT)
public class GlowPasteClient {

    public static final ResourceLocation HIGHLIGHT_MODEL_LOCATION = GlowPaste.identifier("block/glow_highlight");
    public static final ResourceLocation SMALL_HIGHLIGHT_MODEL_LOCATION = GlowPaste.identifier("block/small_glow_highlight");

    public GlowPasteClient(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get(GlowPaste.MODID);
        handler.registerModel(HIGHLIGHT_MODEL_LOCATION);
        handler.registerModel(SMALL_HIGHLIGHT_MODEL_LOCATION);
    }

    public static BakedModel getHighlightModel(){
        return ClientUtils.getMinecraft().getModelManager().getModel(new ModelResourceLocation(HIGHLIGHT_MODEL_LOCATION, "standalone"));
    }

    public static BakedModel getSmallHighlightModel(){
        return ClientUtils.getMinecraft().getModelManager().getModel(new ModelResourceLocation(SMALL_HIGHLIGHT_MODEL_LOCATION, "standalone"));
    }
}
