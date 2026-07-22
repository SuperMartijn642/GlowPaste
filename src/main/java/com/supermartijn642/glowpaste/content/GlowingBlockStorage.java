package com.supermartijn642.glowpaste.content;

import com.supermartijn642.glowpaste.GlowPaste;
import com.supermartijn642.glowpaste.content.packets.SetGlowingPacket;
import com.supermartijn642.glowpaste.extension.ChunkAccessExtension;
import com.supermartijn642.glowpaste.extension.LevelExtension;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Created 21/07/2026 by SuperMartijn642
 */
public abstract class GlowingBlockStorage {

    public static GlowingBlockStorage get(Level level){
        return ((LevelExtension)level).glowPasteGetGlowingBlockStorage();
    }

    @Nullable
    @Contract("_,true->!null")
    public static GlowingBlockStorage.Chunk get(ChunkAccess chunk, boolean createIfAbsent){
        return ((ChunkAccessExtension)chunk).glowPasteGetGlowingBlockStorage(createIfAbsent);
    }

    public abstract boolean has(int x, int y, int z);

    public abstract void set(int x, int y, int z);

    public abstract void clear(int x, int y, int z);

    public static class LevelStorage extends GlowingBlockStorage {
        private final Level level;

        public LevelStorage(Level level){
            this.level = level;
        }

        private GlowingBlockStorage getChunk(int x, int z, boolean createIfAbsent){
            ChunkAccess chunk = this.level.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false);
            return chunk instanceof LevelChunk ? get(chunk, createIfAbsent) : null;
        }

        @Override
        public boolean has(int x, int y, int z){
            GlowingBlockStorage storage = this.getChunk(x, z, false);
            return storage != null && storage.has(x % 16, y, z % 16);
        }

        @Override
        public void set(int x, int y, int z){
            GlowingBlockStorage storage = this.getChunk(x, z, true);
            if(storage != null){
                storage.set(x % 16, y, z % 16);
                this.sendToPlayers(x, y, z, true);
                this.updateLighting(x, y, z);
            }
        }

        @Override
        public void clear(int x, int y, int z){
            GlowingBlockStorage storage = this.getChunk(x, z, false);
            if(storage != null){
                storage.clear(x % 16, y, z % 16);
                this.sendToPlayers(x, y, z, false);
                this.updateLighting(x, y, z);
            }
        }

        private void sendToPlayers(int x, int y, int z, boolean set){
            if(!this.level.isClientSide()){
                SetGlowingPacket packet = new SetGlowingPacket(x, y, z, set);
                for(ServerPlayer player : PlayerLookup.tracking((ServerLevel)this.level, new ChunkPos(x >> 4, z >> 4)))
                    GlowPaste.CHANNEL.sendToPlayer(player, packet);
            }
        }

        private void updateLighting(int x, int y, int z){
            this.level.getChunkSource().getLightEngine().checkBlock(new BlockPos(x, y, z));
        }
    }

    public static class Chunk extends GlowingBlockStorage {
        protected static final int SECTION_SIZE = 16;
        private static final int SECTION_BLOCKS = SECTION_SIZE * SECTION_SIZE * SECTION_SIZE;
        protected static final int SPARSE_CUTOFF = SECTION_BLOCKS / 16; // Sparse section uses one short per block, dense storage uses one bit per block

        protected final ChunkAccess chunk;
        protected final ChunkPos pos;
        protected final int minY;
        protected final Section[] sections;
        private boolean hasChanged = false;

        public Chunk(ChunkAccess chunk, int sections){
            this.chunk = chunk;
            this.pos = chunk.getPos();
            this.minY = this.chunk.getMinY();
            this.sections = new Section[sections];
        }

        public ChunkPos getPos(){
            return this.pos;
        }

        public boolean consumeHasChanged(){
            boolean hasChanged = this.hasChanged;
            this.hasChanged = false;
            return hasChanged;
        }

        protected static int blockToSectionY(int y){
            return (y % 16 + 16) % 16;
        }

        @Override
        public boolean has(int x, int y, int z){
            Section section = this.sections[(y - this.minY) / SECTION_SIZE];
            return section != null && section.has(x, blockToSectionY(y), z);
        }

        @Override
        public void set(int x, int y, int z){
            this.hasChanged = true;
            Section section = this.sections[(y - this.minY) / SECTION_SIZE];
            if(section == null)
                section = this.sections[(y - this.minY) / SECTION_SIZE] = new SparseSection();
            section.set(x, blockToSectionY(y), z);
            if(section.isSparse() && section.size() > SPARSE_CUTOFF){
                DenseSection dense = new DenseSection();
                section.forEach(dense::set);
                this.sections[(y - this.minY) / SECTION_SIZE] = dense;
            }
            this.chunk.markUnsaved();
        }

        @Override
        public void clear(int x, int y, int z){
            Section section = this.sections[(y - this.minY) / SECTION_SIZE];
            if(section == null)
                return;
            this.hasChanged = true;
            section.clear(x, blockToSectionY(y), z);
            if(!section.isSparse() && section.size() < SPARSE_CUTOFF - 10){ // 11 block buffer between sparse-dense conversion
                SparseSection sparse = new SparseSection();
                section.forEach(sparse::set);
                this.sections[(y - this.minY) / SECTION_SIZE] = sparse;
            }
            this.chunk.markUnsaved();
        }

        public void forEach(Consumer consumer){
            int minY = this.chunk.getMinY();
            for(int i = 0; i < this.sections.length; i++){
                Section section = this.sections[i];
                if(section == null)
                    continue;
                int sectionY = minY + i * SECTION_SIZE;
                section.forEach((x, y, z) -> consumer.consume(x, sectionY + y, z));
            }
        }

        public void copyTo(Chunk chunk){
            for(int sectionIndex = 0; sectionIndex < this.sections.length; sectionIndex++){
                Section from = this.sections[sectionIndex];
                if(from == null){
                    chunk.sections[sectionIndex] = null;
                    continue;
                }
                Section to = chunk.sections[sectionIndex];
                if(to == null || to.isSparse() != from.isSparse())
                    to = chunk.sections[sectionIndex] = from.isSparse() ? new SparseSection() : new DenseSection();
                if(from.isSparse()){
                    ((SparseSection)to).data.clear();
                    ((SparseSection)to).data.addAll(((SparseSection)from).data);
                }else{
                    ((DenseSection)to).data.clear();
                    ((DenseSection)to).data.or(((DenseSection)from).data);
                }
            }
        }

        public ClientChunk createClientCopy(){
            ClientChunk chunk = new ClientChunk(this.chunk, this.sections.length);
            this.copyTo(chunk);
            return chunk;
        }

        @Nullable
        public ListTag serialize(){
            ListTag sections = new ListTag();
            for(int sectionIndex = 0; sectionIndex < this.sections.length; sectionIndex++){
                Section section = this.sections[sectionIndex];
                if(section == null)
                    continue;
                int size = section.size();
                if(size == 0)
                    continue;
                CompoundTag sectionData = new CompoundTag();
                sectionData.putInt("index", sectionIndex);
                sectionData.putBoolean("isSparse", section.isSparse());
                if(section.isSparse()){
                    sectionData.putInt("count", size);
                    int longs = Math.ceilDiv(size * 3, 8);
                    long[] data = new long[longs];
                    AtomicInteger index = new AtomicInteger(0);
                    IntConsumer consumer = x -> {
                        int i = index.getAndIncrement();
                        data[i / 16] |= (long)(x & 15) << ((i % 16) * 4);
                    };
                    section.forEach((x, y, z) -> {
                        consumer.accept(x);
                        consumer.accept(y);
                        consumer.accept(z);
                    });
                    sectionData.putLongArray("data", data);
                }else
                    sectionData.putLongArray("data", ((DenseSection)section).data.toLongArray());
                sections.add(sectionData);
            }
            if(sections.isEmpty())
                return null;
            return sections;
        }

        public void deserialize(ListTag tag){
            Section[] sections = new Section[this.sections.length];
            for(Tag t : tag){
                CompoundTag sectionData = (CompoundTag)t;
                int sectionIndex = sectionData.getInt("index").orElseThrow();
                if(sectionData.getBoolean("isSparse").orElseThrow()){
                    Section section = sections[sectionIndex] = new SparseSection();
                    long[] data = sectionData.getLongArray("data").orElseThrow();
                    AtomicInteger index = new AtomicInteger();
                    IntSupplier supplier = () -> {
                        int i = index.getAndIncrement();
                        return (int)(data[i / 16] >> ((i % 16) * 4)) & 15;
                    };
                    for(int i = 0; i < sectionData.getInt("count").orElseThrow(); i++){
                        section.set(
                            supplier.getAsInt(),
                            supplier.getAsInt(),
                            supplier.getAsInt()
                        );
                    }
                }else{
                    DenseSection section = new DenseSection();
                    sections[sectionIndex] = section;
                    long[] data = sectionData.getLongArray("data").orElseThrow();
                    if(data.length > Chunk.SECTION_BLOCKS / 64)
                        throw new IllegalArgumentException("Incorrect data size!");
                    section.data.or(BitSet.valueOf(data));
                }
            }
            System.arraycopy(sections, 0, this.sections, 0, sections.length);
        }

        public interface Consumer {
            void consume(int x, int y, int z);
        }
    }

    public static class ClientChunk extends Chunk {

        private final Section[] isPartialBlockSections;

        private ClientChunk(ChunkAccess chunk, int sections){
            super(chunk, sections);
            this.isPartialBlockSections = new Section[sections];
        }

        public void setPartialBlock(int x, int y, int z){
            Section section = this.isPartialBlockSections[(y - this.minY) / SECTION_SIZE];
            if(section == null)
                section = this.isPartialBlockSections[(y - this.minY) / SECTION_SIZE] = new SparseSection();
            section.set(x, blockToSectionY(y), z);
            if(section.isSparse() && section.size() > SPARSE_CUTOFF){
                DenseSection dense = new DenseSection();
                section.forEach(dense::set);
                this.isPartialBlockSections[(y - this.minY) / SECTION_SIZE] = dense;
            }
        }

        public void forEach(Consumer consumer){
            int minY = this.chunk.getMinY();
            for(int i = 0; i < this.sections.length; i++){
                Section section = this.sections[i];
                if(section == null)
                    continue;
                Section isPartialBlockSection = this.isPartialBlockSections[i];
                int sectionY = minY + i * SECTION_SIZE;
                section.forEach((x, y, z) -> consumer.consume(x, sectionY + y, z, isPartialBlockSection != null && isPartialBlockSection.has(x, y, z)));
            }
        }

        public interface Consumer {
            void consume(int x, int y, int z, boolean isPartialBlock);
        }
    }

    private static abstract class Section extends GlowingBlockStorage {
        public abstract void forEach(Consumer consumer);

        public abstract int size();

        public abstract boolean isSparse();

        public interface Consumer {
            void consume(int x, int y, int z);
        }
    }

    private static class DenseSection extends Section {
        private final BitSet data = new BitSet(Chunk.SECTION_BLOCKS);

        private static int index(int x, int y, int z){
            return x | (z << 4) | (y << 8);
        }

        @Override
        public boolean has(int x, int y, int z){
            return this.data.get(index(x, y, z));
        }

        @Override
        public void set(int x, int y, int z){
            this.data.set(index(x, y, z));
        }

        @Override
        public void clear(int x, int y, int z){
            this.data.clear(index(x, y, z));
        }

        @Override
        public int size(){
            return this.data.length();
        }

        @Override
        public boolean isSparse(){
            return false;
        }

        @Override
        public void forEach(Consumer consumer){
            for(int i = this.data.nextSetBit(0); i >= 0; i = this.data.nextSetBit(i + 1)){
                consumer.consume(
                    i & 15,
                    (i >> 4) & 15,
                    (i >> 8) & 15
                );
            }
        }
    }

    private static class SparseSection extends Section {
        private final ShortSet data = new ShortArraySet(16); // Only iteration is performance critical

        private static short toShort(int x, int y, int z){
            return (short)(x | (y << 4) | (z << 8));
        }

        @Override
        public boolean has(int x, int y, int z){
            return this.data.contains(toShort(x, y, z));
        }

        @Override
        public void set(int x, int y, int z){
            this.data.add(toShort(x, y, z));
        }

        @Override
        public void clear(int x, int y, int z){
            this.data.remove(toShort(x, y, z));
        }

        @Override
        public int size(){
            return this.data.size();
        }

        @Override
        public boolean isSparse(){
            return true;
        }

        @Override
        public void forEach(Consumer consumer){
            ShortIterator iterator = this.data.iterator();
            while(iterator.hasNext()){
                short i = iterator.nextShort();
                consumer.consume(
                    i & 15,
                    (i >> 4) & 15,
                    (i >> 8) & 15
                );
            }
        }
    }
}
