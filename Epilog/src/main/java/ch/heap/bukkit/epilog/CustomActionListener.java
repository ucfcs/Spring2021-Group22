package ch.heap.bukkit.epilog;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class CustomActionListener implements Listener {

    @EventHandler
    public void onFakeEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity().getScoreboardTags().contains("_custom_action")) {
            event.setCancelled(true);
            List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(0.5, 0.5, 0.5);
            nearbyEntities.removeIf(entity -> entity.getType() != EntityType.PLAYER);
            if (nearbyEntities.isEmpty()) {
                return;
            }
            Player user = (Player) nearbyEntities.get(0);
            String action = event.getEntity().getCustomName();
            Bukkit.broadcastMessage("Custom action triggered for user " + user.getName() + ": " + action);
            Bukkit.getPluginManager().callEvent(new CustomActionEvent(user, action));
        }
    }
}
