package ch.heap.bukkit.epilog.meevent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DunesListener implements Listener {
    @EventHandler
    public void onBreakSand(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.SAND) {
            Bukkit.getPluginManager().callEvent(
                new DuneBreakEvent(event.getPlayer(), event.getBlock().getLocation())
            );
        } 
    }
}
