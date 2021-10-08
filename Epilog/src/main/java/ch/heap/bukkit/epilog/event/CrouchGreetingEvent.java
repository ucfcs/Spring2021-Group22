package ch.heap.bukkit.epilog.event;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class CrouchGreetingEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Location location;
    private Player lookingAt;

    public CrouchGreetingEvent(Player player, Location location, Player lookingAt) {
        this.player = player;
        this.location = location;
        this.lookingAt = lookingAt;
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

    public Location getLocation() {
        return location;
    }

    public Player getLookingAt() {
        return lookingAt;
    }
}