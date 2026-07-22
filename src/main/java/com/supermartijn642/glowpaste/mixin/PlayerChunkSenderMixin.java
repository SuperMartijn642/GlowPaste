package com.supermartijn642.glowpaste.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.content.packets.GlowingBlocksPacket;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
@Mixin(PlayerChunkSender.class)
public class PlayerChunkSenderMixin {

    @Inject(
        method = "sendNextChunks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/PlayerChunkSender;sendChunk(Lnet/minecraft/server/network/ServerGamePacketListenerImpl;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;)V",
            shift = At.Shift.AFTER
        )
    )
    private void sendGlowingBlocks(ServerPlayer player, CallbackInfo ci, @Local LevelChunk chunk){
        GlowingBlockStorage.Chunk storage = GlowingBlockStorage.get(chunk, false);
        ListTag data = storage == null ? null : storage.serialize();
        GlowPaste.CHANNEL.sendToPlayer(player, new GlowingBlocksPacket(chunk.getPos(), data));
    }
}
