package com.supermartijn642.glowpaste.content.packets;

import com.supermartijn642.core.network.BlockPosBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.GlowingBlockStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
        Player player = context.getPlayer();
        Level level = context.getWorld();
        if(!level.isLoaded(pos) || !player.getMainHandItem().is(GlowPaste.glowPaste) || !player.getAbilities().mayBuild || !level.mayInteract(player, pos))
            return;
        // Same margin as the server uses for block interactions
        double range = player.blockInteractionRange() + 1.5;
        double dx = pos.getX() + 0.5 - player.getX(), dy = pos.getY() + 0.5 - player.getEyeY(), dz = pos.getZ() + 0.5 - player.getZ();
        if(dx * dx + dy * dy + dz * dz > range * range)
            return;
        GlowingBlockStorage storage = GlowingBlockStorage.get(level);
        if(storage.has(pos.getX(), pos.getY(), pos.getZ()))
            GlowPaste.glowPaste.leftClickBlock(player, player.getMainHandItem(), pos, this.side);
    }
}
