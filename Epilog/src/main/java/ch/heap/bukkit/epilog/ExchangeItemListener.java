package ch.heap.bukkit.epilog;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

public class ExchangeItemListener implements Listener {
    private JavaPlugin plugin;
    public Map<UUID, UUID> itemDroppedByMap = new HashMap<UUID, UUID>();

    public ExchangeItemListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        final UUID itemID = event.getItemDrop().getUniqueId();
        itemDroppedByMap.put(itemID, event.getPlayer().getUniqueId());
        // remove item from map after 10 minutes
        (new BukkitRunnable(){
            @Override
            public void run() {
                itemDroppedByMap.remove(itemID);
            }
        }).runTaskLater(plugin, 10L * 60L * 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // run after data is collected from map
    public void onPickup(EntityPickupItemEvent event) {
        itemDroppedByMap.remove(event.getItem().getUniqueId());
    }

    public void onItemDespawn(ItemDespawnEvent event) {
        itemDroppedByMap.remove(event.getEntity().getUniqueId());
    }
}
