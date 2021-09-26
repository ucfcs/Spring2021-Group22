package ch.heap.bukkit.epilog.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class SpecialItemAttackListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.ENTITY_ATTACK && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack item = attacker.getInventory().getItemInMainHand();
            Material material = item.getType();
            if (material == Material.DIAMOND_SWORD || material == Material.IRON_SWORD) {
                Bukkit.getPluginManager().callEvent(
                    new UsingSpecialItemEvent(attacker, attacker.getLocation(), material == Material.DIAMOND_SWORD ? "strong_sword" : "sword")
                );
            }
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player shooter = (Player) event.getEntity();
            Bukkit.getPluginManager().callEvent(new UsingSpecialItemEvent(shooter, shooter.getLocation(), "bow"));
        }
    }
}
