package com.supermartijn642.glowpaste.content;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.item.BaseItem;
import com.supermartijn642.core.item.ItemProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public class GlowPasteItem extends BaseItem {

    public GlowPasteItem(ItemProperties properties){
        super(properties);
    }

    @Override
    public InteractionFeedback interactWithBlock(ItemStack stack, Player player, InteractionHand hand, Level level, BlockPos hitPos, Direction hitSide, Vec3 hitLocation){
        GlowingBlockStorage glowingBlockStorage = GlowingBlockStorage.get(level);
        if(!glowingBlockStorage.has(hitPos.getX(), hitPos.getY(), hitPos.getZ())){
            if(!level.isClientSide()){
                if(!player.isCreative()){
                    ItemStack adjustedStack = player.getItemInHand(hand).copy();
                    adjustedStack.shrink(1);
                    player.setItemInHand(hand, adjustedStack);
                }
                glowingBlockStorage.set(hitPos.getX(), hitPos.getY(), hitPos.getZ());
            }
            return InteractionFeedback.SUCCESS;
        }
        return InteractionFeedback.PASS;
    }

    @Override
    protected void appendItemInformation(ItemStack stack, Consumer<Component> info, boolean advanced){
        super.appendItemInformation(stack, info, advanced);
        info.accept(TextComponents.translation("glowpaste.glow_paste.description").color(ChatFormatting.GRAY).get());
    }
}
