package ch.heap.bukkit.epilog.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class SpecialItemMarkerListener implements Listener {

    private Set<String> specialItems = new HashSet<>(Arrays.asList(
        "TORCH", "Cave Torch", "WHITE_BANNER"
    ));

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        String displayName = item.hasItemMeta() && !item.getItemMeta().getDisplayName().isEmpty() ? item.getItemMeta().getDisplayName() : item.getType().toString();
        if (specialItems.contains(displayName)) {
            Bukkit.getPluginManager().callEvent(new UsingSpecialItemEvent(event.getPlayer(), event.getPlayer().getLocation(), displayName.toLowerCase().replace(" ", "_")));
        }
    } 
}
