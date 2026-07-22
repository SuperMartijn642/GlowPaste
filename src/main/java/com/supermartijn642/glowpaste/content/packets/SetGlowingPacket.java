package com.supermartijn642.glowpaste.content.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public class SetGlowingPacket implements BasePacket {

    private int x, y, z;
    private boolean set;

    public SetGlowingPacket(int x, int y, int z, boolean set) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.set = set;
    }

    public SetGlowingPacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeVarInt(this.x);
        buffer.writeVarInt(this.y);
        buffer.writeVarInt(this.z);
        buffer.writeBoolean(this.set);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.x = buffer.readVarInt();
        this.y = buffer.readVarInt();
        this.z = buffer.readVarInt();
        this.set = buffer.readBoolean();
    }

    @Override
    public void handle(PacketContext context){
        GlowingBlockStorage storage = GlowingBlockStorage.get(context.getWorld());
        // Setting is already restricted to loaded chunks
        if(this.set)
            storage.set(this.x, this.y, this.z);
        else
            storage.clear(this.x, this.y, this.z);
    }
}
