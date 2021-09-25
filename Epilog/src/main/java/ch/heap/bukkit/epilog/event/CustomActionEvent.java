package ch.heap.bukkit.epilog.event;
 
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
/**
 * Called when an entity with the tag "_custom_action" is spawned. This
 * can be used to trigger special game events for a player that would normally
 * not be caught by regular events. To use, summon an entity with that tag 
 * "_custom_action" and give it a display name of the action to be logged.
 */
public class CustomActionEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private String action;

    public CustomActionEvent(Player player, String action) {
        this.player = player;
        this.action = action;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public String getAction() {
        return action;
    }
}