package ch.heap.bukkit.epilog.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class SpecialItemPotionListener implements Listener {

    private JavaPlugin plugin;
    public SpecialItemPotionListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            ItemStack item = event.getItem();
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            PotionData potion = meta.getBasePotionData();
            Player player = event.getPlayer();
            String potionID = potion.getType().toString().toLowerCase() + "_potion";

            if (potion.getType() == PotionType.SPEED || potion.getType() == PotionType.INVISIBILITY) {
                // Extracting the duration from a bukkit/spigot potion effect
                // is tricker that it looks. This code only seeks to match the 
                // ones explicitely allowed in the game.
                if (potion.getType() == PotionType.SPEED && potion.isExtended()) {
                    SpecialItemEventGenerator.scheduleRepeatingEventGenerator(
                        () -> new UsingSpecialItemEvent(player, player.getLocation(), potionID), 
                        minutes(8), 
                        plugin
                    );
                } else if (potion.getType() == PotionType.INVISIBILITY && potion.isExtended()) {
                    SpecialItemEventGenerator.scheduleRepeatingEventGenerator(
                        () -> new UsingSpecialItemEvent(player, player.getLocation(), potionID), 
                        minutes(8), 
                        plugin
                    );
                }
            } else if (potion.getType() == PotionType.INSTANT_HEAL) {
                if (potion.getType() == PotionType.INSTANT_HEAL && potion.isUpgraded()) {
                    Bukkit.getPluginManager().callEvent(
                        new UsingSpecialItemEvent(player, player.getLocation(), potionID)
                    );
                }
            }
        }
    } 

    private int minutes(int value) {
        return value * 60;
    }
}
