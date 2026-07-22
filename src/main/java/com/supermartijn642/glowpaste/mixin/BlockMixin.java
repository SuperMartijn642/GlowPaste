package com.supermartijn642.glowpaste.mixin;

import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
@Mixin(Block.class)
public class BlockMixin {

    @Inject(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            shift = At.Shift.AFTER
        )
    )
    private static void dropResources(BlockState state, Level level, BlockPos pos, CallbackInfo ci){
        GlowingBlockStorage storage = GlowingBlockStorage.get(level);
        if(storage.has(pos.getX(), pos.getY(), pos.getZ())){
            Block.popResource(level, pos, GlowPaste.glowPaste.getDefaultInstance());
            storage.clear(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Inject(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            shift = At.Shift.AFTER
        )
    )
    private static void dropResources(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity, CallbackInfo ci){
        if(!(level instanceof Level))
            return;
        GlowingBlockStorage storage = GlowingBlockStorage.get((Level)level);
        if(storage.has(pos.getX(), pos.getY(), pos.getZ())){
            Block.popResource((Level)level, pos, GlowPaste.glowPaste.getDefaultInstance());
            storage.clear(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Inject(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            shift = At.Shift.AFTER
        )
    )
    private static void dropResources(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity breaker, ItemStack tool, boolean dropXp, CallbackInfo ci){
        GlowingBlockStorage storage = GlowingBlockStorage.get(level);
        if(storage.has(pos.getX(), pos.getY(), pos.getZ())){
            Block.popResource(level, pos, GlowPaste.glowPaste.getDefaultInstance());
            storage.clear(pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
