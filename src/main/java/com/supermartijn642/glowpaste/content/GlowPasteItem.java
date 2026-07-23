package com.supermartijn642.glowpaste.content;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.item.BaseItem;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.core.util.Pair;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.packets.HitGlowingPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
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

    public boolean leftClickBlock(Player player, ItemStack stack, BlockPos pos, Direction side){
        Level level = player.level();
        GlowingBlockStorage glowingBlocks = GlowingBlockStorage.get(level);
        if(!glowingBlocks.has(pos.getX(), pos.getY(), pos.getZ()))
            return false;
        if(level.isClientSide()){
            GlowPaste.CHANNEL.sendToServer(new HitGlowingPacket(pos, side));
            player.playSound(level.getBlockState(pos).getSoundType().getHitSound());
        }else{
            glowingBlocks.clear(pos.getX(), pos.getY(), pos.getZ());
            if(!player.preventsBlockDrops())
                Block.popResourceFromFace(level, pos, side, GlowPaste.glowPaste.getDefaultInstance());
        }
        return true;
    }

    @Override
    protected void appendItemInformation(ItemStack stack, Consumer<Component> info, boolean advanced){
        super.appendItemInformation(stack, info, advanced);
        info.accept(TextComponents.translation("glowpaste.glow_paste.description").color(ChatFormatting.GRAY).get());
    }
}
