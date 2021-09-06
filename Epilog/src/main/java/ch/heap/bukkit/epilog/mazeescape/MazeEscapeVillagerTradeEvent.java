package ch.heap.bukkit.epilog.mazeescape;
 
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
 
public class MazeEscapeVillagerTradeEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();

    private ItemStack itemStack;
    private Villager villager;
    private Player player;

    public MazeEscapeVillagerTradeEvent(ItemStack itemStack, Villager villager, Player player) {
        this.itemStack = itemStack;
        this.villager = villager;
        this.player = player;
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

    public Villager getVillager() {
        return villager;
    }

    public Player getPlayer() {
        return player;
    }
}