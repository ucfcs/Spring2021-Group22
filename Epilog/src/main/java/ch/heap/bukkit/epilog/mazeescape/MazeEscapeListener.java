package ch.heap.bukkit.epilog.mazeescape;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class MazeEscapeListener implements Listener {

    private static Set<String> specialItems = new HashSet<>(
            Arrays.asList("Glowing Path", "Escape Rope", "Reveal Teammates"));

    @EventHandler
    public void onEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        if (player.isSneaking()) {
            if (specialItems.contains(item.getItemMeta().getDisplayName())) {
                Bukkit.getPluginManager()
                        .callEvent(new MazeEscapeUseSpecialItemEvent(item, player, player.getLocation()));
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
        if (item.getScoreboardTags().contains("trophy")) {
            for (String tag : item.getScoreboardTags()) {
                if (tag.startsWith("trophy") && tag.length() > "trophy".length()) {
                    int trophyNumber = Integer.parseInt(tag.substring("trophy".length()));
                    Bukkit.getPluginManager()
                            .callEvent(new MazeEscapeCollectTrophyEvent(trophyNumber, player, player.getLocation()));
                }
            }
        }
    }

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        InventoryType inventoryType = event.getClickedInventory() != null ? event.getClickedInventory().getType() : null;
        if (inventoryType == InventoryType.MERCHANT && event.getSlotType() == SlotType.RESULT
                && event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();
            Villager villager = (Villager) event.getClickedInventory().getHolder();
            Bukkit.getPluginManager().callEvent(new MazeEscapeVillagerTradeEvent(item, villager, player));
        }
    }
}
