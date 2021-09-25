package ch.heap.bukkit.epilog.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class FarmListener implements Listener {

    @EventHandler
    public void onPlaceWheat(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.WHEAT) {
            Bukkit.getPluginManager().callEvent(new DoFarmEvent(event.getPlayer(), event.getBlock().getLocation(), DoFarmType.PLANT));
        }
    }

    @EventHandler
    public void onBreakWheat(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.WHEAT) {
            Bukkit.getPluginManager().callEvent(new DoFarmEvent(event.getPlayer(), event.getBlock().getLocation(), DoFarmType.HARVEST));
        }
    }
}
