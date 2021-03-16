package com.falcinspire.wc;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;

public class MazeBuilder extends BukkitRunnable {

    private static final int CHUNK_SIZE = 16;
    private World world;
    private Location origin;
    private Location chunkOrigin;
    private int hCuts = 0;
    private int vCuts = 0;
    private boolean[][] grid;
    private int chunkX, chunkZ;

    public MazeBuilder(World world, Location origin, Location chunkOrigin, int hCuts, int vCuts, boolean[][] grid) {
        this.world = world;
        this.origin = origin;
        this.chunkOrigin = chunkOrigin;
        this.hCuts = hCuts;
        this.vCuts = vCuts;
        this.grid = grid;
    }

    public static MazeBuilder create(World world, Location origin, boolean[][] grid) {
        int originX = origin.getBlockX();
        int originY = origin.getBlockY();

        int width = grid[0].length;
        int height = grid.length;

        int clampOriginX = (origin.getBlockX() / CHUNK_SIZE) * CHUNK_SIZE;
        int clampOriginZ = (origin.getBlockZ() / CHUNK_SIZE) * CHUNK_SIZE;
        
        int initialWidth = CHUNK_SIZE - (origin.getBlockX() % CHUNK_SIZE);
        int initialHeight = CHUNK_SIZE - (origin.getBlockZ() % CHUNK_SIZE);

        int leftoverWidth = width - initialWidth;
        int leftoverHeight = height - initialHeight;

        int horizontalCuts = (initialWidth > 0 ? 1 : 0) + (leftoverWidth / CHUNK_SIZE) + (leftoverWidth % CHUNK_SIZE != 0 ? 1 : 0);
        int verticalCuts = (initialHeight > 0 ? 1 : 0) + (leftoverHeight / CHUNK_SIZE) + (leftoverHeight % CHUNK_SIZE != 0 ? 1 : 0);

        return new MazeBuilder(world, origin, new Location(world, clampOriginX, origin.getBlockY(), clampOriginZ), horizontalCuts, verticalCuts, grid);
    }

    @Override
    public void run() {
        int width = grid[0].length;
        int height = grid.length;

        int baseY = Math.max(0, origin.getBlockY() - 50);

        Bukkit.broadcastMessage(chunkX + " " + chunkZ);
        for (int z = chunkOrigin.getBlockZ() + chunkZ * CHUNK_SIZE; z < chunkOrigin.getBlockZ() + (chunkZ+1) * CHUNK_SIZE; z++) {
            for (int x = chunkOrigin.getBlockX() + chunkX * CHUNK_SIZE; x < chunkOrigin.getBlockX() + (chunkX+1) * CHUNK_SIZE; x++) {
                boolean xInSrc = x >= origin.getBlockX() && x < origin.getBlockX() + width;
                boolean zInSrc = z >= origin.getBlockZ() && z < origin.getBlockZ() + height;
                if (xInSrc && zInSrc) {
                    if (grid[z-origin.getBlockZ()][x-origin.getBlockX()]) {
                        for (int y = 0; y < 200; y++) {
                            world.getBlockAt(x, baseY + y, z).setType(Material.BEDROCK);
                        }
                    }
                }
            }
        }
        chunkX++;
        if (chunkX == hCuts) {
            chunkX = 0;
            chunkZ++;
        }
        if (chunkZ == vCuts) {
            this.cancel();
        }
    }

}