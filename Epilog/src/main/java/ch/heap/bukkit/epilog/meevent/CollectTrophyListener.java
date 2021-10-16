package ch.heap.bukkit.epilog.meevent;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class CollectTrophyListener implements Listener {
    @EventHandler
    public void onEvent(EntityPickupItemEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity();
        Item item = event.getItem();
        if (item.getScoreboardTags().contains("trophy")) {
            for (String tag : item.getScoreboardTags()) {
                if (tag.startsWith("trophy") && tag.length() > "trophy".length()) {
                    int trophyNumber = Integer.parseInt(tag.substring("trophy".length()));
                    Bukkit.getPluginManager()
                            .callEvent(new CollectTrophyEvent(trophyNumber, player, player.getLocation()));
                }
            }
        }
    }
}
