package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
        method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V",
        at = @At("TAIL")
    )
    private void updateLevelInEngines(ClientLevel level, boolean stopSound, CallbackInfo ci){
        GlowingBlockHighlighter.clear();
    }

    @Inject(
        method = "startAttack()Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void cancelChiselLeftClickBlock(CallbackInfoReturnable<Boolean> cir){
        Player player = ClientUtils.getPlayer();
        if(player.isSpectator())
            return;
        ItemStack stack = player.getMainHandItem();
        if(stack.getItem() == GlowPaste.glowPaste){
            BlockHitResult result = (BlockHitResult)ClientUtils.getMinecraft().hitResult;
            assert result != null;
            if(!ClientUtils.getWorld().getWorldBorder().isWithinBounds(result.getBlockPos()))
                return;
            if(GlowPaste.glowPaste.leftClickBlock(player, stack, result.getBlockPos(), result.getDirection()))
                player.swing(InteractionHand.MAIN_HAND);
            cir.setReturnValue(true);
        }
    }
}
