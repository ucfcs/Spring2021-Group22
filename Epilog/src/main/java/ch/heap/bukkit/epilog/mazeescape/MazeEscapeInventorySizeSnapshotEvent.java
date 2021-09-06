package ch.heap.bukkit.epilog.mazeescape;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class MazeEscapeInventorySizeSnapshotEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private int inventorySize;

    public MazeEscapeInventorySizeSnapshotEvent(Player player, int inventorySize) {
        this.player = player;
        this.inventorySize = inventorySize;
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

    public int getInventorySize() {
        return inventorySize;
    }
}