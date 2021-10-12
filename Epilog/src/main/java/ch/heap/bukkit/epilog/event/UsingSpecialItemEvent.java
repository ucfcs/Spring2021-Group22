package ch.heap.bukkit.epilog.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UsingSpecialItemEvent extends Event implements MazeEscapeEvent {
    private static final HandlerList handlers = new HandlerList();
    
    public final Player player;
    public final Location location;
    public final String action;

    public UsingSpecialItemEvent(Player player, Location location, String action) {
        this.player = player;
        this.location = location;
        this.action = action;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
