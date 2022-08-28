package com.extremelyd1.world.biome;

import com.extremelyd1.game.Game;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.List;

public class BiomeFinderThread extends Thread {

    /**
     * The world to search for biomes in
     */
    private final World searchWorld;

    /**
     * Whether the search is done
     */
    private boolean done;

    private final BiomeDetectionController controller;

    public BiomeFinderThread(World world, int chunkSampling) {
        this.searchWorld = world;
        controller = new BiomeDetectionController(world, chunkSampling);

        done = false;
    }

    @Override
    public void run() {
        if (!done) {
            Game.getLogger().info("Starting biome detection for world " + searchWorld.getName());
            controller.detectBiomes();
            done = true;
            Game.getLogger().info("Biome detection complete for " + searchWorld.getName() + ". " + getFoundBiomes().size() + " biomes found.");
        }
    }

    public boolean isDone() {
        return done;
    }

    public List<Biome> getFoundBiomes() {
        return controller.getLoadedBiomes();
    }
}
