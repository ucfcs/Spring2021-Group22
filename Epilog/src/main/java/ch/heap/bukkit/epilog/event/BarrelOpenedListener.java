package ch.heap.bukkit.epilog.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BarrelOpenedListener implements Listener {
    @EventHandler
    public void onBarrelOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BARREL) {
            Bukkit.getPluginManager().callEvent(
                new BarrelOpenedEvent(event.getPlayer(), event.getClickedBlock().getLocation())
            );
        }
    }
}
