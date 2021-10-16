package ch.heap.bukkit.epilog;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLocationRunnable extends BukkitRunnable {

    private JavaPlugin plugin;

    public PlayerLocationRunnable(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerLocationEvent(p, p.getLocation()));
        }
    }
}
