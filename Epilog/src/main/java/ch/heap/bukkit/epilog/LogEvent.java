package ch.heap.bukkit.epilog;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class LogEvent {
	public Event event;
	public long time;
	public Player player = null;
	public String eventName = null;
	public Material material; // cache material for BlockBreakEvent
	public String experimentLabel;
	public boolean ignore = false;
	public boolean needsData = false;
	public Map <String, Object> data = new HashMap <String, Object>();
	
	public LogEvent() {};
	public LogEvent(String name, long time, String experimentLabel, Player player) {
		this.eventName = name;
		this.time = time;
		this.experimentLabel = experimentLabel;
		this.player = player;
	}

	public Document toDocument() {
		Document doc = new Document(this.data);

		Player p = this.player;
		if (p!=null) {
			doc.append("player", p.getUniqueId().toString());
		}
		if (experimentLabel != null) {
			doc.append("experimentLabel", experimentLabel);
		}
		doc.append("time", this.time);
		doc.append("event", this.eventName);

		return doc;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> data = new HashMap<>(this.data);
		Player p = this.player;
		if (p!=null) {
			data.put("player", p.getUniqueId().toString());
		}
		if (experimentLabel != null) {
			data.put("experimentLabel", experimentLabel);
		}
		data.put("time", this.time);
		data.put("event", this.eventName);
		return data;
	}
}
