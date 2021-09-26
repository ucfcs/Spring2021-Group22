package ch.heap.bukkit.epilog.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;

public class VillagerTradeListener implements Listener {
    @EventHandler
    public void onVillagerTrade(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getClickedInventory() == null) {
            return;
        }
        if (event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (!(event.getClickedInventory().getHolder() instanceof Villager)) {
            return;
        }
        if (event.getInventory().getType() == InventoryType.MERCHANT && event.getSlotType() == SlotType.RESULT) {
            Player player = (Player) event.getWhoClicked();
            Villager villager = (Villager) event.getClickedInventory().getHolder();
            Bukkit.getPluginManager().callEvent(
                new VillagerTradeEvent(player, villager.getLocation(), event.getCurrentItem())
            );
        } 
    }
}
