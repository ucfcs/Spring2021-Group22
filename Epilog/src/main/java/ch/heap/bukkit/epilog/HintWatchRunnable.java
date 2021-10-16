package ch.heap.bukkit.epilog;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import ch.heap.bukkit.epilog.meevent.UsingSpecialItemEvent;

import org.bukkit.inventory.meta.BookMeta;

public class HintWatchRunnable extends BukkitRunnable {

    private JavaPlugin plugin;

    public HintWatchRunnable(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            ItemStack item = p.getInventory().getItemInMainHand();
            String displayName = null;
            if (item.getType() == Material.WRITTEN_BOOK) {
                displayName = item.hasItemMeta() ? ((BookMeta)item.getItemMeta()).getTitle() : "";
            } else {
                displayName = item.hasItemMeta() && !item.getItemMeta().getDisplayName().isEmpty() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : "";
            }
            if (displayName.startsWith("Hint ")) {
                String hintID = "hint_" + displayName.substring("Hint #".length());
                Bukkit.getPluginManager().callEvent(new UsingSpecialItemEvent(p, p.getLocation(), hintID));
            }
        }
    }
}
