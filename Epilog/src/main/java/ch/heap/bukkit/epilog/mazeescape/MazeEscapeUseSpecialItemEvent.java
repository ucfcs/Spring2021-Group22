package ch.heap.bukkit.epilog.mazeescape;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
 
public class MazeEscapeUseSpecialItemEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private ItemStack itemStack;
    private Player player;
    private Location location;

    public MazeEscapeUseSpecialItemEvent(ItemStack itemStack, Player player, Location location) {
        this.itemStack = itemStack;
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

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }
}