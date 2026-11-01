package com.supermartijn642.glowpaste;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
public class GlowPasteClient implements ClientModInitializer {

    public static final ResourceLocation HIGHLIGHT_MODEL_LOCATION = GlowPaste.identifier("block/glow_highlight");
    public static final ResourceLocation SMALL_HIGHLIGHT_MODEL_LOCATION = GlowPaste.identifier("block/small_glow_highlight");

    @Override
    public void onInitializeClient(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get(GlowPaste.MODID);
        handler.registerModel(HIGHLIGHT_MODEL_LOCATION);
        handler.registerModel(SMALL_HIGHLIGHT_MODEL_LOCATION);
    }

    public static BakedModel getHighlightModel(){
        return ClientUtils.getMinecraft().getModelManager().getModel(HIGHLIGHT_MODEL_LOCATION);
    }

    public static BakedModel getSmallHighlightModel(){
        return ClientUtils.getMinecraft().getModelManager().getModel(SMALL_HIGHLIGHT_MODEL_LOCATION);
    }
}
