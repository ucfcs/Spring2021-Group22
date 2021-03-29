package ch.heap.bukkit.epilog;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MapEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String message;

    public MapEvent(String example) {
        message = example;
    }

	@Override
	public String getEventName() {
		return "MapEvent";
	}

    public String getMessage() {
        return message;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}