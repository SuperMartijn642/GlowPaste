package com.supermartijn642.glowpaste.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.GlowPasteClient;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created 22/07/2026 by SuperMartijn642
 */
public class GlowingBlockHighlighter {

    private static final PoseStack POSE_STACK = new PoseStack();

    private static int lastCameraChunkX, lastCameraChunkZ;
    private static GlowingBlockStorage.ClientChunk[] glowingBlocks;

    private static State state;

    public static void clear(){
        lastCameraChunkX = 0;
        lastCameraChunkZ = 0;
        glowingBlocks = null;
    }

    public static void extractHighlights(ClientLevel level, LevelRenderState levelRenderState, Camera camera, int viewDistance){
        Player player = ClientUtils.getPlayer();
        if(!player.getMainHandItem().is(GlowPaste.glowPaste) && !player.getOffhandItem().is(GlowPaste.glowPaste)){
            state = State.EMPTY;
            if(glowingBlocks != null)
                glowingBlocks = null;
            return;
        }
        int cameraChunkX = camera.blockPosition().getX() >> 4, cameraChunkZ = camera.blockPosition().getZ() >> 4;
        viewDistance = Math.min(5, viewDistance);
        int chunkColumns = viewDistance * 2 + 1;
        int chunkCount = chunkColumns * chunkColumns;
        if(glowingBlocks == null || glowingBlocks.length != chunkCount)
            glowingBlocks = new GlowingBlockStorage.ClientChunk[chunkCount];
        else if(cameraChunkX != lastCameraChunkX || cameraChunkZ != lastCameraChunkZ){
            // Shift grid over
            int offsetX = cameraChunkX - lastCameraChunkX, offsetZ = cameraChunkZ - lastCameraChunkZ;
            boolean reversedX = offsetX < 0, reversedZ = offsetZ < 0;
            for(int x = reversedX ? chunkColumns - 1 : 0; x >= 0 && x < chunkColumns; x += (reversedX ? -1 : 1)){
                for(int z = reversedZ ? chunkColumns - 1 : 0; z >= 0 && z < chunkColumns; z += (reversedZ ? -1 : 1)){
                    if(x + offsetX >= 0 && x + offsetX < chunkColumns && z + offsetZ >= 0 && z + offsetZ < chunkColumns)
                        glowingBlocks[x * chunkColumns + z] = glowingBlocks[(x + offsetX) * chunkColumns + z + offsetZ];
                    else
                        glowingBlocks[x * chunkColumns + z] = null;
                }
            }
        }
        // Update chunk copies
        BlockPos.MutableBlockPos dummyBlockPos = new BlockPos.MutableBlockPos();
        for(int x = 0; x < chunkColumns; x++){
            for(int z = 0; z < chunkColumns; z++){
                int chunkX = cameraChunkX - viewDistance + x, chunkZ = cameraChunkZ - viewDistance + z;
                ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
                GlowingBlockStorage.Chunk chunkGlowingBlocks = chunk == null ? null : GlowingBlockStorage.get(chunk, false);
                if(chunk == null || !doesChunkHaveAllNeighbors(level, chunkX, chunkZ) || chunkGlowingBlocks == null){
                    glowingBlocks[x * chunkColumns + z] = null;
                    continue;
                }
                GlowingBlockStorage.ClientChunk clientChunk = glowingBlocks[x * chunkColumns + z];
                boolean isNew = false;
                if(clientChunk == null){
                    clientChunk = glowingBlocks[x * chunkColumns + z] = chunkGlowingBlocks.createClientCopy();
                    isNew = true;
                }else
                    chunkGlowingBlocks.copyTo(clientChunk);
                if(chunkGlowingBlocks.consumeHasChanged() || isNew){
                    int chunkStartX = chunk.getPos().getMinBlockX(), chunkStartZ = chunk.getPos().getMinBlockZ();
                    GlowingBlockStorage.ClientChunk finalClientChunk = clientChunk;
                    clientChunk.forEach((xx, yy, zz) -> {
                        dummyBlockPos.set(chunkStartX + xx, yy, chunkStartZ + zz);
                        BlockState state = chunk.getBlockState(dummyBlockPos);
                        VoxelShape shape = state.getVisualShape(EmptyBlockGetter.INSTANCE, dummyBlockPos, CollisionContext.empty());
                        if(shape.isEmpty() ||
                            ((shape.max(Direction.Axis.X) - shape.min(Direction.Axis.X)) < 0.9 &&
                            (shape.max(Direction.Axis.Y) - shape.min(Direction.Axis.Y)) < 0.9 &&
                            (shape.max(Direction.Axis.Z) - shape.min(Direction.Axis.Z)) < 0.9))
                            finalClientChunk.setPartialBlock(xx, yy, zz);
                    });
                }
            }
        }
        lastCameraChunkX = cameraChunkX;
        lastCameraChunkZ = cameraChunkZ;
        state = new State(glowingBlocks);
    }

    private static boolean hasChunk(ClientLevel level, int x, int z){
        ChunkAccess chunk = level.getChunk(x, z, ChunkStatus.FULL, false);
        return chunk != null && level.getLightEngine().lightOnInColumn(SectionPos.getZeroNode(SectionPos.asLong(x, 0, z)));
    }

    public static boolean doesChunkHaveAllNeighbors(ClientLevel level, int x, int z){
        return hasChunk(level, x - 1, z)
            && hasChunk(level, x, z - 1)
            && hasChunk(level, x + 1, z)
            && hasChunk(level, x, z + 1)
            && hasChunk(level, x - 1, z - 1)
            && hasChunk(level, x - 1, z + 1)
            && hasChunk(level, x + 1, z - 1)
            && hasChunk(level, x + 1, z + 1);
    }

    public static void submitHighlights(LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector){
        State state = GlowingBlockHighlighter.state;
        if(state == null || state.glowingBlocks.length == 0)
            return;
        PoseStack poseStack = POSE_STACK;
        poseStack.pushPose();
        Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        for(GlowingBlockStorage.ClientChunk glowingBlocks : state.glowingBlocks){
            if(glowingBlocks == null)
                continue;
            ChunkPos chunkPos = glowingBlocks.getPos();
            int chunkStartX = chunkPos.getMinBlockX(), chunkStartZ = chunkPos.getMinBlockZ();
            glowingBlocks.forEach((x, y, z, isPartialBlock) -> {
                poseStack.pushPose();
                poseStack.translate(chunkStartX + x, y, chunkStartZ + z);
                submitNodeCollector.submitBlockModel(
                    poseStack,
                    Sheets.cutoutBlockItemSheet(),
                    isPartialBlock ? GlowPasteClient.SMALL_HIGHLIGHT_MODEL_PARTS : GlowPasteClient.HIGHLIGHT_MODEL_PARTS,
                    new int[0],
                    LightCoordsUtil.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    0
                );
                poseStack.popPose();
            });
        }
        poseStack.popPose();
    }

    private record State(GlowingBlockStorage.ClientChunk[] glowingBlocks) {
        static final State EMPTY = new State(new GlowingBlockStorage.ClientChunk[0]);
    }
}
