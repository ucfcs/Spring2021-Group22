package ch.heap.bukkit.epilog.event;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class CollectTrophyEvent extends Event implements MazeEscapeEvent  {

    private static final HandlerList handlers = new HandlerList();

    private int trophyNumber;
    private Player player;
    private Location location;

    public CollectTrophyEvent(int trophyNumber, Player player, Location location) {
        this.trophyNumber = trophyNumber;
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

    public int getTrophyNumber() {
        return trophyNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }
}