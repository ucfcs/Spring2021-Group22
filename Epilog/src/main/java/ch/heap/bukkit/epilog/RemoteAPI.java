package ch.heap.bukkit.epilog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;


import org.bson.Document;

public class RemoteAPI {
	private Epilog plugin;
	private DatabaseDriver db;
	private Document doc;

	// 10000 events: about 2.5 MB (250 KB compressed); 1000 player seconds
	private int logCacheLimit = 100000; // 25 MB; 2.7 player hours
	// limite sending of large caches; adapts to number of new events

	private BlockingQueue<Document> documentQueue = new LinkedBlockingQueue<>();
	private Postman postman = null;

	public RemoteAPI(Epilog plugin, DatabaseDriver db) {
		this.plugin = plugin;
		this.db = db;
	}

	public void start() {
		if (!this.plugin.isEnabled())
			return;
		// loadLogCache();
		this.addLogEvent(this.plugin.epilogStateEvent("connect", true));
		if (postman == null) {
			postman = new Postman();
			postman.start();
		}

		Map<UUID, Location> prevLocation = new HashMap<>();

		BukkitScheduler scheduler = plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : plugin.getServer().getOnlinePlayers()) {

					Location loc = p.getLocation();
					Location prev = prevLocation.get(p.getUniqueId());

					if (prev != null && prevLocation.get(p.getUniqueId()).equals(loc)) {
						return;
					}

					prevLocation.put(p.getUniqueId(), loc);

					Map<String, Object> data = new HashMap<>();

					data.put("x", loc.getX());
					data.put("y", loc.getY());
					data.put("z", loc.getZ());
					data.put("pitch", loc.getPitch());
					data.put("yaw", loc.getYaw());

					data.put("player", p.getUniqueId().toString());

					data.put("time", System.currentTimeMillis());

					data.put("experimentLabel", plugin.activeExperimentLabel);

					data.put("event", "PlayerLocationEvent");

					Document doc = new Document(data);
					documentQueue.add(doc);
				}
			}
		}, 0L, 20L);
	}

	public void stop() {
		if (postman != null) {
			postman.interrupt();
			try {
				// postman.join();
				System.out.println("postman interupt");
				// no more ongoing web requests now
			} catch (Exception e) {
				Bukkit.getServer().broadcastMessage(e.getMessage());
			}
			postman = null;
		}
		this.addLogEvent(this.plugin.epilogStateEvent("disconnect", false));

		try {
			while (!documentQueue.isEmpty()) {
				doc = documentQueue.take();
				db.sendData(doc);
			}
		} catch (Exception e) {
			Bukkit.getServer().broadcastMessage(e.getMessage());
		}

		Bukkit.getServer().broadcastMessage("queue cleared");
	}

	public void addLogEvent(Document doc) {
		documentQueue.add(doc);
	}

	public void addLogEvent(LogEvent event) {

		// System.out.println("113" + event.toString());
		documentQueue.add(event.toDocument());
	}

	public void addLogData(Map<String, Object> data) {
		Document doc = new Document(data);
		documentQueue.add(doc);
	}

	// New thread to write to MongoDB
	private class Postman extends Thread {
		private Document doc;

		public void run() {
			try {
				while (true) {
					doc = documentQueue.take();
					
					db.sendData(doc);
				}
			} catch (InterruptedException ignore) {
				Bukkit.getServer().broadcastMessage("postman thread interupted");
				System.err.println("postman thread interupted");
			}
		}
	}
}
