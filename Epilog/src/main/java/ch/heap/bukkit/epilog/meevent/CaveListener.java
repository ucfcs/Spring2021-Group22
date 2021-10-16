package ch.heap.bukkit.epilog.meevent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CaveListener implements Listener {
    @EventHandler
    public void onBreakOre(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();
        if (blockType == Material.COAL_ORE || blockType == Material.IRON_ORE || blockType == Material.GOLD_ORE || blockType == Material.DIAMOND_ORE) {
            Bukkit.getPluginManager().callEvent(
                new OreBreakEvent(event.getPlayer(), event.getBlock().getLocation(), event.getBlock().getType())
            );
        } 
    }
}
