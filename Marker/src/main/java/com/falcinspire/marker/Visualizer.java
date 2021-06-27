package com.falcinspire.marker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

public class Visualizer {
    public static void showMarker(Location location, Material material) {
        FallingBlock block = location.getWorld().spawnFallingBlock(
            location, 
            new ItemStack(material).getData()
        );
        block.setTicksLived(500);
        block.setInvulnerable(true);
        block.setGlowing(true);
        block.setGravity(false);
        block.setDropItem(false);
    }
}
