package com.supermartijn642.glowpaste.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow
    private ServerLevel level;

    @Inject(
        method = "destroyBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z",
            shift = At.Shift.BEFORE
        )
    )
    private void captureIsGlowing(BlockPos pos, CallbackInfoReturnable<Boolean> ci, @Share("isGlowing") LocalBooleanRef isGlowing) {
        isGlowing.set(GlowingBlockStorage.get(this.level).has(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Inject(
        method = "destroyBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;mineBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V",
            shift = At.Shift.BEFORE
        )
    )
    private void dropGlowPaste(BlockPos pos, CallbackInfoReturnable<Boolean> ci, @Share("isGlowing") LocalBooleanRef isGlowing) {
        if(isGlowing.get())
            Block.popResource(this.level, pos, GlowPaste.glowPaste.getDefaultInstance());
    }
}
