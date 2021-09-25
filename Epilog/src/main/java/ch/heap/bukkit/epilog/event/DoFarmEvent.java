package ch.heap.bukkit.epilog.event;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class DoFarmEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Location location;
    private DoFarmType type;

    public DoFarmEvent(Player player, Location location, DoFarmType type) {
        this.player = player;
        this.location = location;
        this.type = type;
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

    public DoFarmType getType() {
        return type;
    }
}