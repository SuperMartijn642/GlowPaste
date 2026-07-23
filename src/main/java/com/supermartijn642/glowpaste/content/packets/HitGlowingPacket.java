package com.supermartijn642.glowpaste.content.packets;

import com.supermartijn642.core.network.BlockPosBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created 23/07/2026 by SuperMartijn642
 */
public class HitGlowingPacket extends BlockPosBasePacket {

    private Direction side;

    public HitGlowingPacket(BlockPos pos, Direction side){
        super(pos);
        this.side = side;
    }

    public HitGlowingPacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        super.write(buffer);
        buffer.writeByte(this.side.get3DDataValue());
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        super.read(buffer);
        this.side = Direction.from3DDataValue(buffer.readByte());
    }

    @Override
    protected void handle(BlockPos pos, PacketContext context){
        if(!context.getWorld().isLoaded(pos))
            return;
        GlowingBlockStorage storage = GlowingBlockStorage.get(context.getWorld());
        if(storage.has(pos.getX(), pos.getY(), pos.getZ()))
            GlowPaste.glowPaste.leftClickBlock(context.getPlayer(), context.getPlayer().getMainHandItem(), pos, this.side);
    }
}
