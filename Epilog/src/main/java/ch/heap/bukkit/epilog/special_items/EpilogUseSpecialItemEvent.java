package ch.heap.bukkit.epilog.special_items;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class EpilogUseSpecialItemEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private String itemID;
    private Player player;
    private Location location;

    public EpilogUseSpecialItemEvent(String itemID, Player player, Location location) {
        this.itemID = itemID;
        this.player = player;
        this.location = location;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getItemID() {
        return itemID;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }
}