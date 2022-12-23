package me.maartin0.dimensionalutil.util;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class VoidChunkGenerator extends ChunkGenerator {
    public static class PlainsBiomeProvider extends BiomeProvider {
        @NotNull
        @Override
        public Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
            return Biome.PLAINS;
        }

        @NotNull
        @Override
        public List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
            return List.of(Biome.PLAINS);
        }
    }

    void clearChunkData(ChunkData chunkData) {
        chunkData.setRegion(0, chunkData.getMinHeight(), 0, 15, chunkData.getMaxHeight(), 15, Material.VOID_AIR);
    }

    @Override public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.@NotNull ChunkData chunkData) {
        clearChunkData(chunkData);
    }

    @Override public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.@NotNull ChunkData chunkData) {
        clearChunkData(chunkData);
    }

    @Override public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.@NotNull ChunkData chunkData) {
        clearChunkData(chunkData);
    }

    @Override public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.@NotNull ChunkData chunkData) {
        clearChunkData(chunkData);
    }

    @Override public boolean shouldGenerateBedrock() { return false; }
    @Override public boolean shouldGenerateCaves() { return false; }
    @Override public boolean shouldGenerateDecorations() { return false; }
    @Override public boolean shouldGenerateMobs() { return false; }
    @Override public boolean shouldGenerateNoise() { return false; }
    @Override public boolean shouldGenerateStructures() { return false; }
    @Override public boolean shouldGenerateSurface() { return false; }
}
