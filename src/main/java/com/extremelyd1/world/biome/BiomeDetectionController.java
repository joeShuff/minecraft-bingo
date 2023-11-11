package com.extremelyd1.world.biome;

import com.extremelyd1.game.Game;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BiomeDetectionController {

    private World world;

    private List<Biome> loadedBiomes = new ArrayList<>();

    private int chunkSampling = 2;

    private int percentageComplete = 0;

    public BiomeDetectionController(World world, int chunkSampling) {
        this.world = world;
        this.chunkSampling = chunkSampling;
    }

    public void detectBiomes() {
        Chunk centerChunk = world.getWorldBorder().getCenter().getChunk();

        int chunksInBorder = (int) Math.ceil(world.getWorldBorder().getSize() / 16.0d);
        int chunksAroundCenter = (int) Math.ceil(chunksInBorder / 2.0d);

        double totalChunks = (int) Math.pow(Math.ceil(chunksInBorder / (double) chunkSampling), 2);
        int chunksChecked = 0;

        ArrayList<Biome> foundBiomes = new ArrayList<>();

        for (int x = -chunksAroundCenter; x < chunksAroundCenter; x+= chunkSampling) {
            for (int z = -chunksAroundCenter; z < chunksAroundCenter; z += chunkSampling) {
                Chunk thisChunk = world.getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);
                ChunkSnapshot snapshot = thisChunk.getChunkSnapshot(true, true, true);

                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y += 48) {
                    Biome thisBiome = snapshot.getBiome(8, y, 8);

                    if (!foundBiomes.contains(thisBiome)) {
                        foundBiomes.add(thisBiome);
                    }
                }

                chunksChecked++;

                percentageComplete = (int) ((chunksChecked / totalChunks) * 100.0);
                Game.getLogger().info("[Biome detector] " + world.getName() + ": " + chunksChecked + "/" + totalChunks + " (" + percentageComplete + "%)");
            }
        }

        loadedBiomes = foundBiomes;
    }

    public List<Biome> getLoadedBiomes() {
        return loadedBiomes;
    }

    public int percentageComplete() {
        return percentageComplete;
    }

}
