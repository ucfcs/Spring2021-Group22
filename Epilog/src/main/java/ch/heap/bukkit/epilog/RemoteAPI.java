package ch.heap.bukkit.epilog;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;


import org.bson.Document;

public class RemoteAPI {
	private Epilog plugin;
	private DatabaseDriver db;
	private ScheduledFuture<?> future;

	private BlockingQueue<Document> documentQueue = new LinkedBlockingQueue<>();

	public RemoteAPI(Epilog plugin, DatabaseDriver db) {
		this.plugin = plugin;
		this.db = db;
	}

	public void start() {
		if (!this.plugin.isEnabled())
			return;

		if (future == null) {
			ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
			future = exec.scheduleAtFixedRate(() -> {
				while (!documentQueue.isEmpty()) {
					try {
						db.sendData(documentQueue.take());
					} catch (Exception e) {
						Bukkit.getServer().broadcastMessage(e.getMessage());
					}
				}
			}, 0, 10, TimeUnit.SECONDS);
		}
	}

	public void stop() {
		if (future != null) {
			future.cancel(true);
		}

		try {
			while (!documentQueue.isEmpty()) {
				db.sendData(documentQueue.take());
			}
		} catch (Exception e) {
			Bukkit.getServer().broadcastMessage(e.getMessage());
		}

	}

	public void addLogEvent(Document doc) {
		documentQueue.add(doc);
	}

	public void addLogEvent(LogEvent event) {
		documentQueue.add(event.toDocument());
	}

	public void addLogData(Map<String, Object> data) {
		Document doc = new Document(data);
		documentQueue.add(doc);
	}	
}
