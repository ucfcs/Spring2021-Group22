package ch.heap.bukkit.epilog.event;
 
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class OreBreakEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Location location;
    private Material material;

    public void setMaterial(Material material) {
        this.material = material;
    }

    public OreBreakEvent(Player player, Location location, Material material) {
        this.player = player;
        this.location = location;
        this.material = material;
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

    public Material getMaterial() {
        return material;
    }
}