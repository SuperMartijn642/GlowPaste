package com.supermartijn642.glowpaste.content.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import com.supermartijn642.glowpaste.extension.ChunkAccessExtension;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public class GlowingBlocksPacket implements BasePacket {

    private ChunkPos chunkPos;
    private ListTag tag;

    public GlowingBlocksPacket(ChunkPos chunkPos, ListTag tag){
        this.chunkPos = chunkPos;
        this.tag = tag;
    }

    public GlowingBlocksPacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeChunkPos(this.chunkPos);
        if(this.tag == null)
            buffer.writeBoolean(false);
        else{
            buffer.writeBoolean(true);
            buffer.writeNbt(this.tag);
        }
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.chunkPos = buffer.readChunkPos();
        if(buffer.readBoolean())
            this.tag = (ListTag)buffer.readNbt(NbtAccounter.defaultQuota());
        else
            this.tag = null;
    }

    @Override
    public void handle(PacketContext context){
        ChunkAccess chunk = context.getWorld().getChunk(this.chunkPos.x(), this.chunkPos.z(), ChunkStatus.FULL, false);
        if(chunk == null)
            return;
        GlowingBlockStorage.Chunk storage = GlowingBlockStorage.get(chunk, this.tag != null);
        if(storage != null)
            storage.deserialize(this.tag == null ? new ListTag() : this.tag);
    }
}
