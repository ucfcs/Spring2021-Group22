package ch.heap.bukkit.epilog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.bson.Document;

public class RemoteAPI {
	private Epilog plugin;
	private DatabaseDriver db;
	private Document doc;

	// 10000 events: about 2.5 MB (250 KB compressed); 1000 player seconds
	private int logCacheLimit = 100000; // 25 MB; 2.7 player hours
	// limite sending of large caches; adapts to number of new events

	private ArrayDeque<Map<String, Object>> pendingLogs = new ArrayDeque<>();

	private BlockingQueue<Document> documentQueue = new LinkedBlockingQueue<>();
	// private BlockingQueue<Request> requests = new LinkedBlockingQueue<Request>();
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
	}

	public void stop() {
		if (postman != null) {
			postman.interrupt();
			try {
				postman.join();
				// no more ongoing web requests now
			} catch (InterruptedException e) {
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
		} catch (InterruptedException e) {
			Bukkit.getServer().broadcastMessage(e.getMessage());
		}

		Bukkit.getServer().broadcastMessage("queue cleared");
	}

	public void addLogEvent(Document doc) {

		documentQueue.add(doc);

		// this.db.sendData(doc);
	}

	public void addLogEvent(LogEvent event) {
		// Document doc = event.toDocument();
		documentQueue.add(event.toDocument());
		// this.db.sendData(doc);
	}

	public void addLogData(Map<String, Object> data) {
		Document doc = new Document(data);
		// this.db.sendData(doc);
		documentQueue.add(doc);
	}

	// private void dispatchLogQueue(boolean notifyPlugins) {
	// if (this.logSendRequestPending)
	// return;
	// int n = logQueue.size() + this.pendingLogs.size();
	// if (n == 0)
	// return;
	// this.logSendRequestPending = true;

	// final int logSize = n;
	// Request request = new Request("log", new RequestDelegate() {
	// @Override
	// public void response(boolean success, JSONObject answer) {
	// if (success) {
	// pendingLogs.clear();
	// }
	// if (answer != null) {
	// skippedLogs -= answer.optInt("skippedLogs", 0);
	// }
	// logSendRequestPending = false;
	// }
	// });

	// int newLogs = logQueue.size() - previousLogSize;
	// if (newLogs > this.logSendLimit) {
	// // queue is growing faster than we are sending
	// this.logSendLimit = newLogs;
	// }
	// previousLogSize = logQueue.size();

	// if (logSize > this.logCacheLimit) {
	// // add 1 to include new logSkipEvent
	// final int toSkip = logSize - this.logCacheLimit + 1;
	// int skipped = 0;
	// while (toSkip > skipped) {
	// Map<String, Object> log = this.pendingLogs.poll();
	// if (log == null)
	// break;
	// skipped += 1;
	// }
	// while (toSkip > skipped) {
	// Map<String, Object> log = this.logQueue.poll();
	// if (log == null)
	// break;
	// skipped += 1;
	// }
	// this.skippedLogs += skipped;
	// Map<String, Object> data = new HashMap<String, Object>();
	// data.put("skipped", skipped);
	// data.put("logSize", logSize);
	// this.plugin.postEvent("logSkipEvent", null, data, true);
	// this.plugin.getLogger().warning("log cache is full; skipping " + skipped + "
	// events");
	// }

	// if (n > this.logSendLimit)
	// n = this.logSendLimit;
	// while (this.pendingLogs.size() < n) {
	// Map<String, Object> log = this.logQueue.poll();
	// if (log == null)
	// break;
	// this.pendingLogs.add(log);
	// }
	// previousLogSize = this.logQueue.size();

	// if (this.skippedLogs != 0) {
	// request.info.put("skippedLogs", this.skippedLogs);
	// }
	// request.setData(this.pendingLogs);
	// // give connected plugins a chance to add request info
	// for (String pluginName : this.plugin.connectedPlugins) {
	// if (!notifyPlugins)
	// continue;
	// Plugin plugin =
	// this.plugin.getServer().getPluginManager().getPlugin(pluginName);
	// if (plugin == null)
	// continue;
	// try {
	// Method method = plugin.getClass().getMethod("onLogSendRequestPrepare",
	// Map.class);
	// method.invoke(plugin, request.info);
	// } catch (Exception e) {
	// }
	// }
	// this.addRequest(request);
	// }

	// executes requester in Postman thread
	// public boolean offerCustomRequest(final Runnable requester) {
	// Request request = new Request(new RequestDelegate() {
	// @Override
	// public void response(boolean success, JSONObject answer) {
	// requester.run();
	// }
	// });
	// request.requiresServerToken = false;
	// addRequest(request);
	// return true;
	// }

	// public boolean offerWorlds(String cause, World only) {
	// LogEvent event = new LogEvent("Worlds", System.currentTimeMillis(), null);
	// event.data.put("worlds", plugin.dataCollector.getWorlds(only));
	// event.data.put("cause", "token");
	// Request request = new Request("worlds", event.toMap(), null);
	// sendRequest(request);
	// return true;
	// }

	// private void sendRequests(Runnable then) {
	// Request request;
	// while ((request = requests.poll()) != null) {
	// request.dispatchTime = System.currentTimeMillis();
	// sendRequest(request);
	// }
	// if (then != null) {
	// then.run();
	// }
	// }

	// private void sendTokenRequest() {
	// LogEvent event = new LogEvent("TokenRequest", System.currentTimeMillis(),
	// null);
	// plugin.dataCollector.addServerMetaData(event);
	// event.data.put("port", this.plugin.getServer().getPort());
	// String id = this.getPrivateServerID();
	// event.data.put("epilogServerID", id);

	// Request request = new Request("token", event.toMap(), new RequestDelegate() {
	// @Override
	// public void response(boolean success, JSONObject answer) {
	// if (answer == null)
	// return;
	// String token = answer.optString("serverToken", null);
	// String id = answer.optString("epilogServerID", null);
	// if (token != null) {
	// serverToken = token;
	// // trigger first successful log queue dispatch
	// postman.sendHeartBeatAt = 0;
	// }
	// if (id != null) {
	// setPrivateServerID(id);
	// }
	// skippedLogs -= answer.optInt("skippedLogs", 0);
	// // send worlds
	// offerWorlds("token", null);
	// }
	// });
	// if (this.skippedLogs != 0) {
	// request.info.put("skippedLogs", this.skippedLogs);
	// }
	// request.requiresServerToken = false;
	// sendRequest(request);
	// }

	// private void sendHeartBeat() {
	// Document data = new Document();
	// data.append("key", "value");
	// data.append("time", (int)System.currentTimeMillis());
	// //data.append("currentVersion", plugin.getCurrentVersion());
	// //data.append("config", plugin.config);
	// //data.append("port", plugin.getServer().getPort()); // int
	// //data.append("serverID", this.getPrivateServerID());
	// //Request request = new Request("heartbeat", data, null);
	// //request.requiresServerToken = false;
	// //sendRequest(request);

	// db.sendData(data);
	// }

	// handle events/actions initiated by the server
	// private void handleRequestResponse(JSONObject answer) {
	// JSONArray events = answer.optJSONArray("events");
	// if (events != null) {
	// long time = System.currentTimeMillis();
	// int length = events.length();
	// for (int i = 0; i < length; i++) {
	// LogEvent event = LogEvent.fromJSON(events.getJSONObject(i));
	// event.time = time;
	// event.ignore = true; // don't send back to logging server
	// this.plugin.postEvent(event);
	// }
	// }
	// JSONObject plugins = answer.optJSONObject("plugins");
	// if (plugins != null && !plugin.bukkitMode) {
	// // this.plugin.updater.setAvailablePlugins(plugins);
	// }
	// }

	// public interface RequestDelegate {
	// public void response(boolean success, JSONObject answer);
	// }

	// public class Request {
	// public String cmd = null;
	// public Collection<Map<String, Object>> data = null;
	// public RequestDelegate delegate = null;
	// public boolean callDelegateInGameLoop = false;
	// public boolean requiresServerToken = true;
	// public long dispatchTime = 0;
	// public Map<String, Object> info = new HashMap<>();

	// public Request(String cmd, RequestDelegate delegate) {
	// this.cmd = cmd;
	// this.delegate = delegate;
	// }

	// public Request(RequestDelegate delegate) {
	// // dummy request to allow sending request to other servers
	// // within the delegate (no request made if cmd==null)
	// this.delegate = delegate;
	// }

	// public Request(String cmd, Map<String, Object> data, RequestDelegate
	// delegate) {
	// this.cmd = cmd;
	// this.setData(data);
	// this.delegate = delegate;
	// }

	// public void setData(ArrayDeque<Map<String, Object>> data) {
	// this.data = data;
	// }

	// public void setData(Map<String, Object> data) {
	// if (data == null) {
	// this.data = null;
	// } else {
	// ArrayList<Map<String, Object>> dataList = new ArrayList<>();
	// dataList.add(data);
	// this.data = dataList;
	// }
	// }

	// public void addInfo(Map<String, Object> info) {
	// if (info == null)
	// return;
	// for (String key : info.keySet()) {
	// this.info.put(key, info.get(key));
	// }
	// }

	// public void response(final boolean success, final JSONObject answer) {
	// if (this.delegate == null)
	// return;
	// if (this.callDelegateInGameLoop) {
	// if (!plugin.isEnabled())
	// return;
	// new BukkitRunnable() {
	// @Override
	// public void run() {
	// delegate.response(success, answer);
	// }
	// }.runTask(plugin);
	// } else {
	// delegate.response(success, answer);
	// }
	// }
	// }

	// thread to send web requests
	private class Postman extends Thread {
		private Document doc;

		public void run() {
			try {
				while (true) {
					doc = documentQueue.take();
					System.out.println("logging");

					System.out.println(doc);

					db.sendData(doc);
				}
			} catch (InterruptedException ignore) {
				Bukkit.getServer().broadcastMessage("postman interupted");	
			}
		}
	}

	// private void sendRequest(Request request) {

	// /*for (Player p : plugin.getServer().getOnlinePlayers()) {
	// if (plugin.notifications && p.hasPermission("epilog.notifications")) {
	// p.sendMessage("[epilog] TODO: make post request");
	// if (request.data != null) {
	// for (JSONObject json : request.data) {
	// p.sendMessage(json.toString());
	// }
	// }
	// }
	// }
	// plugin.getLogger().info("TODO: make post request");
	// if (request.data != null) {
	// for (JSONObject json : request.data) {
	// plugin.getLogger().info(json.toString());
	// }
	// }*/

	// for (Map<String, Object> req : request.data) {
	// Document doc = new Document(req);
	// db.sendData(doc);
	// }
	// }

}
