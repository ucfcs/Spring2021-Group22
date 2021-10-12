package ch.heap.bukkit.epilog.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import ch.heap.bukkit.epilog.CustomActionEvent;

public class SpecialItemUsageListener implements Listener {

    private JavaPlugin plugin;
    public SpecialItemUsageListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCustomEvent(CustomActionEvent event) {
        final Player player = event.getPlayer();
        final String action = event.getAction();

        if (event.getAction().contentEquals(SpecialItemActions.USE_GLOW_PATH.action)) {
            SpecialItemEventGenerator.scheduleRepeatingEventGenerator(
                () -> new UsingSpecialItemEvent(player, player.getLocation(), action), 
                60, 
                plugin
            );
        } else if (event.getAction().contentEquals(SpecialItemActions.USE_ESCAPE_ROPE.action)) {
            Bukkit.getPluginManager().callEvent(
                new UsingSpecialItemEvent(player, player.getLocation(), action)
           );
        } else if (event.getAction().contentEquals(SpecialItemActions.USE_REVEAL_PLAYERS.action)) {
            SpecialItemEventGenerator.scheduleRepeatingEventGenerator(
                () -> new UsingSpecialItemEvent(player, player.getLocation(), action), 
                20, 
                // Effect lasts 20 seconds
                plugin
            );
        }
    }

    @EventHandler
    public void onUseSpecialPickaxe(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();
        if (blockType == Material.NETHER_BRICKS || blockType == Material.CRACKED_NETHER_BRICKS) {
           Bukkit.getPluginManager().callEvent(
               new UsingSpecialItemEvent(event.getPlayer(), event.getPlayer().getLocation(), "nether_brick_pickaxe")
           );
        }
    }

    @EventHandler
    public void onUseTorch(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.WHITE_BANNER) {
            Bukkit.getPluginManager().callEvent(
                new UsingSpecialItemEvent(event.getPlayer(), event.getPlayer().getLocation(), "white_banner")
            );   
        } else if (event.getBlock().getType() == Material.TORCH) {
            Bukkit.getPluginManager().callEvent(
                new UsingSpecialItemEvent(event.getPlayer(), event.getPlayer().getLocation(), "torch")
            );
        }
    }

    @EventHandler
    //TODO TEST THIS AND OTHERS
    public void onUseFireworks(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.FIREWORK) {
            Firework firework = (Firework) event.getEntity();
            if (firework.getShooter() instanceof Player) {
                Player player = (Player) firework.getShooter();
                Bukkit.getPluginManager().callEvent(
                    new UsingSpecialItemEvent(player, player.getLocation(), "fireworks")
                );
            }
        }
    }
}
