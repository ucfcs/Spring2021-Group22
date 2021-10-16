package ch.heap.bukkit.epilog;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import ch.heap.bukkit.epilog.meevent.UsingSpecialItemEvent;

public class ArmorWatchRunnable extends BukkitRunnable {

    private JavaPlugin plugin;

    public ArmorWatchRunnable(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            for (ItemStack armorItem : p.getInventory().getArmorContents()) {
                if (armorItem != null && armorItem.hasItemMeta()) {
                    String displayName = armorItem.getItemMeta().getDisplayName();
                    Bukkit.getPluginManager().callEvent(new UsingSpecialItemEvent(p, p.getLocation(), displayName.toLowerCase().replace(" ", "_")));
                }
            }
        }
    }
}
