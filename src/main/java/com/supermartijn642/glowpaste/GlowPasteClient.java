package com.supermartijn642.glowpaste;

import com.supermartijn642.core.registry.ClientRegistrationHandler;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
public class GlowPasteClient {

    public static final String HIGHLIGHT_MODEL_LOCATION = "block/glow_highlight", SMALL_HIGHLIGHT_MODEL_LOCATION = "block/small_glow_highlight";
    public static List<BlockStateModelPart> HIGHLIGHT_MODEL_PARTS = List.of(), SMALL_HIGHLIGHT_MODEL_PARTS = List.of();

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get(GlowPaste.MODID);
        handler.registerBlockStateModelConsumer(HIGHLIGHT_MODEL_LOCATION, model -> {
            //noinspection DataFlowIssue,deprecation
            model.collectParts(null, HIGHLIGHT_MODEL_PARTS = new ArrayList<>(1));
        });
        handler.registerBlockStateModelConsumer(SMALL_HIGHLIGHT_MODEL_LOCATION, model -> {
            //noinspection DataFlowIssue,deprecation
            model.collectParts(null, SMALL_HIGHLIGHT_MODEL_PARTS = new ArrayList<>(1));
        });
    }
}
