package ch.heap.bukkit.epilog.meevent;

import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpecialItemEventGenerator {
    
    public static void scheduleRepeatingEventGenerator(Supplier<Event> supplier, int duration, JavaPlugin plugin) {
        (new BukkitRunnable(){
            private int seconds = 0;
            @Override
            public void run() {
                if (seconds == duration) {
                    this.cancel();
                }
                Bukkit.getPluginManager().callEvent(supplier.get());
                this.seconds++;
            }
        }).runTaskTimer(plugin, 0L, 20L);
    }
}
