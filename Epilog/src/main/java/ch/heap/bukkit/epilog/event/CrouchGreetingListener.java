package ch.heap.bukkit.epilog.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CrouchGreetingListener implements Listener {

    private Map<UUID, Data> lastCrouched = new HashMap<>();

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        lastCrouched.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Data data = lastCrouched.get(event.getPlayer().getUniqueId());
            if (data == null) {
                lastCrouched.put(event.getPlayer().getUniqueId(), new Data(System.currentTimeMillis(), 1));
                return;
            }
            long curTime = System.currentTimeMillis();
            final long ONE_SECOND = 1000;
            if (curTime - data.lastCrouchedTime <= ONE_SECOND) {
                lastCrouched.put(event.getPlayer().getUniqueId(), new Data(curTime, data.streak+1));
                if (data.streak+1 >= 3) {
                    Bukkit.getPluginManager().callEvent(
                        new CrouchGreetingEvent(event.getPlayer(), event.getPlayer().getLocation())
                    );
                }
            } else {
                lastCrouched.put(event.getPlayer().getUniqueId(), new Data(curTime, 1));
            }
        }
    }

    private static class Data {
        final long lastCrouchedTime;
        final int streak;

        public Data(long lastCrouchedTime, int streak) {
            this.lastCrouchedTime = lastCrouchedTime;
            this.streak = streak;
        }

        @Override
        public String toString() {
            return "{lastCrouchedTime=" + lastCrouchedTime + ", streak=" + streak + "}";
        }
    }
}
