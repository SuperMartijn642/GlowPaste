package com.supermartijn642.glowpaste;

import com.supermartijn642.core.registry.ClientRegistrationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.block.model.BlockStateModel;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
public class GlowPasteClient implements ClientModInitializer {

    public static final String HIGHLIGHT_MODEL_LOCATION = "block/glow_highlight", SMALL_HIGHLIGHT_MODEL_LOCATION = "block/small_glow_highlight";
    public static BlockStateModel HIGHLIGHT_MODEL, SMALL_HIGHLIGHT_MODEL;

    @Override
    public void onInitializeClient(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get(GlowPaste.MODID);
        handler.registerBlockModelConsumer(HIGHLIGHT_MODEL_LOCATION, model -> HIGHLIGHT_MODEL = model);
        handler.registerBlockModelConsumer(SMALL_HIGHLIGHT_MODEL_LOCATION, model -> SMALL_HIGHLIGHT_MODEL = model);
    }
}
