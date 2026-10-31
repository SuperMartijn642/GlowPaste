package com.supermartijn642.glowpaste;

import com.supermartijn642.core.registry.ClientRegistrationHandler;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod(value = GlowPaste.MODID, dist = Dist.CLIENT)
public class GlowPasteClient {

    public static final String HIGHLIGHT_MODEL_LOCATION = "block/glow_highlight", SMALL_HIGHLIGHT_MODEL_LOCATION = "block/small_glow_highlight";
    public static BakedModel HIGHLIGHT_MODEL, SMALL_HIGHLIGHT_MODEL;

    public GlowPasteClient(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get(GlowPaste.MODID);
        handler.registerModelConsumer(HIGHLIGHT_MODEL_LOCATION, model -> HIGHLIGHT_MODEL = model);
        handler.registerModelConsumer(SMALL_HIGHLIGHT_MODEL_LOCATION, model -> SMALL_HIGHLIGHT_MODEL = model);
    }
}
