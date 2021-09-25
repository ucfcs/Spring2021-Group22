package ch.heap.bukkit.epilog.event;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
 
public class VillagerTradeEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Location location;
    private ItemStack acquiredItemStack;

    public VillagerTradeEvent(Player player, Location location, ItemStack acquiredItemStack) {
        this.player = player;
        this.location = location;
        this.acquiredItemStack = acquiredItemStack;
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ItemStack getAcquiredItemStack() {
        return acquiredItemStack;
    }

    public void setAcquiredItemStack(ItemStack acquiredItemStack) {
        this.acquiredItemStack = acquiredItemStack;
    }
}