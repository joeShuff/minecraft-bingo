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

    private Plugin plugin;

    private World world;

    private List<Biome> loadedBiomes = new ArrayList<>();

    private int chunkSampling = 2;

    public BiomeDetectionController(World world, int chunkSampling) {
        this.world = world;
        this.chunkSampling = chunkSampling;
    }

    public void detectBiomes() {
        Chunk centerChunk = world.getWorldBorder().getCenter().getChunk();

        int chunksInBorder = (int) Math.ceil(world.getWorldBorder().getSize() / 16.0d);
        int chunksAroundCenter = (int) Math.ceil(chunksInBorder / 2.0d);

        ArrayList<Biome> foundBiomes = new ArrayList<>();

        for (int x = -chunksAroundCenter; x < chunksAroundCenter; x+= chunkSampling) {
            for (int z = -chunksAroundCenter; z < chunksAroundCenter; z += chunkSampling) {
                Chunk thisChunk = world.getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);
                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y += 48) {
                    Block thisBlock = thisChunk.getBlock(8, y, 8);
                    Location checkLocation = thisBlock.getLocation();

                    if (checkLocation.getY() > world.getHighestBlockYAt(thisBlock.getX(), thisBlock.getZ())) {
                        break;
                    }

                    Biome thisBiome = world.getBiome(checkLocation);

                    if (!foundBiomes.contains(thisBiome)) {
                        foundBiomes.add(thisBiome);
                    }
                }
            }
        }

        loadedBiomes = foundBiomes;
    }

    public List<Biome> getLoadedBiomes() {
        return loadedBiomes;
    }

}
