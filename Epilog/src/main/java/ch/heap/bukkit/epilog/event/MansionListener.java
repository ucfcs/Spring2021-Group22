package ch.heap.bukkit.epilog.event;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MansionListener implements Listener {

    @EventHandler
    public void onSolvePin(CustomActionEvent event) {
        if (event.getAction().contentEquals("type_correct_pin")) {
            Bukkit.getPluginManager().callEvent(
                new SolveMansionPuzzleEvent(event.getPlayer(), event.getPlayer().getLocation(), MansionPuzzleType.PIN)
            );
        } else if (event.getAction().contentEquals("open_lever_iron_door")) {
            Bukkit.getPluginManager().callEvent(
                new SolveMansionPuzzleEvent(event.getPlayer(), event.getPlayer().getLocation(), MansionPuzzleType.LEVER_DOOR)
            );
        } else if (event.getAction().contentEquals("open_shared_iron_door")) {
            Bukkit.getPluginManager().callEvent(
                new SolveMansionPuzzleEvent(event.getPlayer(), event.getPlayer().getLocation(), MansionPuzzleType.SHARED_DOOR)
            );
        }
    }

    @EventHandler
    public void onFindBarrel(BarrelOpenedEvent event) {
        Bukkit.getPluginManager().callEvent(
            new SolveMansionPuzzleEvent(event.getPlayer(), event.getPlayer().getLocation(), MansionPuzzleType.HIDDEN_BARREL)
        );
    }
}
