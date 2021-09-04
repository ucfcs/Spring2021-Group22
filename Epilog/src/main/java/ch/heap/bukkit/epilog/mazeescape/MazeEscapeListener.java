package ch.heap.bukkit.epilog.mazeescape;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class MazeEscapeListener implements Listener {

    private static Set<String> specialItems = new HashSet<>(Arrays.asList("Glowing Path", "Escape Rope", "Reveal Teammates"));

    @EventHandler
    public void onEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        if (player.isSneaking()) {
            if (specialItems.contains(item.getItemMeta().getDisplayName())) {
                Bukkit.getPluginManager().callEvent(
                    new MazeEscapeUseSpecialItemEvent(item, player, player.getLocation())
                );
            }
        }
    }

    @EventHandler
    public void onEvent(EntityPickupItemEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity();
        Item item = event.getItem();
        if (item.getScoreboardTags().contains("Trophy")) {
            for (String tag : item.getScoreboardTags()) {
                if (tag.startsWith("Trophy") && tag.length() > "Trophy".length()) {
                    int trophyNumber = Integer.parseInt(tag.substring("Trophy".length()));
                    Bukkit.getPluginManager().callEvent(
                        new MazeEscapeCollectTrophyEvent(trophyNumber, player, player.getLocation())
                    );
                }
            }
        }
    }
    
}
