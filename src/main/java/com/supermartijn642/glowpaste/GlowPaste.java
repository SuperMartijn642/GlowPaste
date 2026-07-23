package com.supermartijn642.glowpaste;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.core.network.PacketDirection;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.core.registry.RegistryEntryAcceptor;
import com.supermartijn642.glowpaste.content.GlowPasteItem;
import com.supermartijn642.glowpaste.content.packets.GlowingBlocksPacket;
import com.supermartijn642.glowpaste.content.packets.HitGlowingPacket;
import com.supermartijn642.glowpaste.content.packets.SetGlowingPacket;
import com.supermartijn642.glowpaste.generators.*;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(GlowPaste.MODID)
public class GlowPaste {

    public static final String MODID = "glowpaste";

    public static Identifier identifier(String path){
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    public static final Logger LOGGER = CommonUtils.getLogger(MODID);
    public static final PacketChannel CHANNEL = PacketChannel.create(MODID);

    @RegistryEntryAcceptor(namespace = MODID, identifier = "glow_paste", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static GlowPasteItem glowPaste;

    public GlowPaste(){
        CHANNEL.registerMessage(SetGlowingPacket.class, SetGlowingPacket::new, PacketDirection.SERVER_TO_CLIENT, true);
        CHANNEL.registerMessage(GlowingBlocksPacket.class, GlowingBlocksPacket::new, PacketDirection.SERVER_TO_CLIENT, true);
        CHANNEL.registerMessage(HitGlowingPacket.class, HitGlowingPacket::new, PacketDirection.CLIENT_TO_SERVER, true);

        register();
        registerGenerators();
    }

    private static void register(){
        RegistrationHandler handler = RegistrationHandler.get(MODID);
        handler.registerItem("glow_paste", () -> new GlowPasteItem(ItemProperties.create().group(CreativeItemGroup.getIngredients())));
    }

    public static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get(MODID);
        handler.addProvider(generator -> new GlowPasteFusionTextureMetadataProvider(MODID, generator.getPackOutput()));
        handler.addGenerator(cache -> new GlowPasteItemInfoGenerator(MODID, cache));
        handler.addGenerator(cache -> new GlowPasteLanguageGenerator(MODID, cache));
        handler.addGenerator(cache -> new GlowPasteModelGenerator(MODID, cache));
        handler.addGenerator(cache -> new GlowPasteRecipeGenerator(MODID, cache));
    }
}
