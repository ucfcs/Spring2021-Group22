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
import org.json.JSONArray;
import org.json.JSONObject;

import org.bson.Document;

public class RemoteAPI {
	private Epilog plugin;
	private DatabaseDriver db;
	private String serverToken = null;

	// 10000 events: about 2.5 MB (250 KB compressed); 1000 player seconds
	private int logCacheLimit = 100000; // 25 MB; 2.7 player hours
	// limite sending of large caches; adapts to number of new events
	private int logSendLimit = 5000;
	private int previousLogSize = Integer.MAX_VALUE;
	private boolean logSendRequestPending = false;
	private ArrayDeque<JSONObject> pendingLogs = new ArrayDeque<JSONObject>();
	public int skippedLogs = 0;

	private BlockingQueue<JSONObject> logQueue = new LinkedBlockingQueue<JSONObject>();
	private BlockingQueue<Request> requests = new LinkedBlockingQueue<Request>();
	private Postman postman = null;

	public RemoteAPI(Epilog plugin, DatabaseDriver db) {
		this.plugin = plugin;
		this.db = db;
	}

	public void start() {
		if (!this.plugin.isEnabled())
			return;
		//loadLogCache();
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
			}
			postman = null;
		}
		this.addLogEvent(this.plugin.epilogStateEvent("disconnect", false));
		dispatchLogQueue(false);
		// now send all pending requests in this thread
		sendRequests(new Runnable() {
			@Override
			public void run() {
				// we are sure now that all log send requests are finished
				// TODO: WRITE ALL REMAINING REQUESTS
				//saveLogCache();
				serverToken = null;
			}
		});
	}

	// heart beats will trigger updates
	public void triggerHeartBeat() {
		if (this.postman == null)
			return;
		this.postman.sendHeartBeatAt = 0;
	}

	public void addLogEvent(LogEvent event) {
		//this.logQueue.add(event.toJSON());


		//Document doc = Document.parse(event.data.toString());
		System.out.println(event.data.toString().replace('=', ':'));
		Document doc = new Document();
		doc.append("data", event.data.toString().replace('=', ':'));

		this.db.sendData(doc);
	}

	public void addLogData(JSONObject data) {
		//this.logQueue.add(data);

		//Document doc = Document.parse(data.toString());
		System.out.println(data.toString().replace('=', ':'));
		Document doc = new Document();
		doc.append("data", data.toString().replace('=', ':'));

		this.db.sendData(doc);
	}

	public void accessRequest(String email) {
		final JSONObject data = new JSONObject();
		String serverID = this.getPrivateServerID();
		if (serverID == null)
			return;
		data.put("epilogServerID", serverID);
		data.put("email", email);
		data.put("serverName", this.plugin.getServer().getServerName());
		Request request = new Request("access", data, null);
		this.addRequest(request);
		// TODO: add response handler to provide feedback
	}

	public String getPrivateServerID() {
		File keyFile = new File(this.plugin.getDataFolder(), "private_server_id");
		try {
			Scanner ss = new Scanner(keyFile);
			String id = ss.useDelimiter("\\Z").next().trim();
			ss.close();
			if (id.length() == 0)
				return null;
			return id;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public void setPrivateServerID(String id) {
		File keyFile = new File(this.plugin.getDataFolder(), "private_server_id");
		try {
			PrintWriter writer = new PrintWriter(keyFile, "UTF-8");
			writer.print(id);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*private void loadLogCache() {
		File cacheFile = new File(this.plugin.getDataFolder(), "log_cache.json");
		if (!cacheFile.exists())
			return;
		try {
			BufferedReader br = new BufferedReader(new FileReader(cacheFile));
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject event = new JSONObject(line);
				this.logQueue.add(event);
			}
			br.close();
		} catch (Exception e) {
		}
		cacheFile.delete();
	}*/

	/*private void saveLogCache() {
		if (this.logQueue.size() + this.pendingLogs.size() == 0)
			return;
		File cacheFile = new File(this.plugin.getDataFolder(), "log_cache.json");
		try {
			PrintWriter writer = new PrintWriter(cacheFile, "UTF-8");
			JSONObject log;
			while ((log = this.pendingLogs.poll()) != null) {
				writer.println(log.toString());
			}
			while ((log = this.logQueue.poll()) != null) {
				writer.println(log.toString());
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	// request stuff

	private void dispatchLogQueue(boolean notifyPlugins) {
		if (this.logSendRequestPending)
			return;
		int n = logQueue.size() + this.pendingLogs.size();
		if (n == 0)
			return;
		this.logSendRequestPending = true;

		final int logSize = n;
		Request request = new Request("log", new RequestDelegate() {
			@Override
			public void response(boolean success, JSONObject answer) {
				if (success) {
					pendingLogs.clear();
				}
				if (answer != null) {
					skippedLogs -= answer.optInt("skippedLogs", 0);
				}
				logSendRequestPending = false;
			}
		});

		int newLogs = logQueue.size() - previousLogSize;
		if (newLogs > this.logSendLimit) {
			// queue is growing faster than we are sending
			this.logSendLimit = newLogs;
		}
		previousLogSize = logQueue.size();

		if (logSize > this.logCacheLimit) {
			// add 1 to include new logSkipEvent
			final int toSkip = logSize - this.logCacheLimit + 1;
			int skipped = 0;
			while (toSkip > skipped) {
				JSONObject log = this.pendingLogs.poll();
				if (log == null)
					break;
				skipped += 1;
			}
			while (toSkip > skipped) {
				JSONObject log = this.logQueue.poll();
				if (log == null)
					break;
				skipped += 1;
			}
			this.skippedLogs += skipped;
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("skipped", skipped);
			data.put("logSize", logSize);
			this.plugin.postEvent("logSkipEvent", null, data, true);
			this.plugin.getLogger().warning("log cache is full; skipping " + skipped + " events");
		}

		if (n > this.logSendLimit)
			n = this.logSendLimit;
		while (this.pendingLogs.size() < n) {
			JSONObject log = this.logQueue.poll();
			if (log == null)
				break;
			this.pendingLogs.add(log);
		}
		previousLogSize = this.logQueue.size();

		if (this.skippedLogs != 0) {
			request.info.put("skippedLogs", this.skippedLogs);
		}
		request.setData(this.pendingLogs);
		// give connected plugins a chance to add request info
		for (String pluginName : this.plugin.connectedPlugins) {
			if (!notifyPlugins)
				continue;
			Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(pluginName);
			if (plugin == null)
				continue;
			try {
				Method method = plugin.getClass().getMethod("onLogSendRequestPrepare", JSONObject.class);
				method.invoke(plugin, request.info);
			} catch (Exception e) {
			}
		}
		this.addRequest(request);
	}

	public void addRequest(Request request) {
		this.requests.add(request);
	}

	// executes requester in Postman thread
	public boolean offerCustomRequest(final Runnable requester) {
		Request request = new Request(new RequestDelegate() {
			@Override
			public void response(boolean success, JSONObject answer) {
				requester.run();
			}
		});
		request.requiresServerToken = false;
		addRequest(request);
		return true;
	}

	public boolean offerWorlds(String cause, World only) {
		LogEvent event = new LogEvent("Worlds", System.currentTimeMillis(), null);
		event.data.put("worlds", plugin.dataCollector.getWorlds(only));
		event.data.put("cause", "token");
		Request request = new Request("worlds", event.toJSON(), null);
		sendRequest(request);
		return true;
	}

	private void sendRequests(Runnable then) {
		Request request;
		while ((request = requests.poll()) != null) {
			request.dispatchTime = System.currentTimeMillis();
			sendRequest(request);
		}
		if (then != null) {
			then.run();
		}
	}

	private void sendTokenRequest() {
		LogEvent event = new LogEvent("TokenRequest", System.currentTimeMillis(), null);
		plugin.dataCollector.addServerMetaData(event);
		event.data.put("port", this.plugin.getServer().getPort());
		String id = this.getPrivateServerID();
		event.data.put("epilogServerID", id);

		Request request = new Request("token", event.toJSON(), new RequestDelegate() {
			@Override
			public void response(boolean success, JSONObject answer) {
				if (answer == null)
					return;
				String token = answer.optString("serverToken", null);
				String id = answer.optString("epilogServerID", null);
				if (token != null) {
					serverToken = token;
					// trigger first successful log queue dispatch
					postman.sendHeartBeatAt = 0;
				}
				if (id != null) {
					setPrivateServerID(id);
				}
				skippedLogs -= answer.optInt("skippedLogs", 0);
				// send worlds
				offerWorlds("token", null);
			}
		});
		if (this.skippedLogs != 0) {
			request.info.put("skippedLogs", this.skippedLogs);
		}
		request.requiresServerToken = false;
		sendRequest(request);
	}

	private void sendHeartBeat() {
		Document data = new Document();
		data.append("key", "value");
		data.append("time", (int)System.currentTimeMillis());
		//data.append("currentVersion", plugin.getCurrentVersion());
		//data.append("config", plugin.config);
		//data.append("port", plugin.getServer().getPort()); // int
		//data.append("serverID", this.getPrivateServerID());
		//Request request = new Request("heartbeat", data, null);
		//request.requiresServerToken = false;
		//sendRequest(request);

		db.sendData(data);
	}

	// handle events/actions initiated by the server
	private void handleRequestResponse(JSONObject answer) {
		JSONArray events = answer.optJSONArray("events");
		if (events != null) {
			long time = System.currentTimeMillis();
			int length = events.length();
			for (int i = 0; i < length; i++) {
				LogEvent event = LogEvent.fromJSON(events.getJSONObject(i));
				event.time = time;
				event.ignore = true; // don't send back to logging server
				this.plugin.postEvent(event);
			}
		}
		JSONObject plugins = answer.optJSONObject("plugins");
		if (plugins != null && !plugin.bukkitMode) {
			// this.plugin.updater.setAvailablePlugins(plugins);
		}
	}

	public interface RequestDelegate {
		public void response(boolean success, JSONObject answer);
	}

	public class Request {
		public String cmd = null;
		public Collection<JSONObject> data = null;
		public RequestDelegate delegate = null;
		public boolean callDelegateInGameLoop = false;
		public boolean requiresServerToken = true;
		public long dispatchTime = 0;
		public JSONObject info = new JSONObject();

		public Request(String cmd, RequestDelegate delegate) {
			this.cmd = cmd;
			this.delegate = delegate;
		}

		public Request(RequestDelegate delegate) {
			// dummy request to allow sending request to other servers
			// within the delegate (no request made if cmd==null)
			this.delegate = delegate;
		}

		public Request(String cmd, JSONObject data, RequestDelegate delegate) {
			this.cmd = cmd;
			this.setData(data);
			this.delegate = delegate;
		}

		public void setData(ArrayDeque<JSONObject> data) {
			this.data = data;
		}

		public void setData(JSONObject data) {
			if (data == null) {
				this.data = null;
			} else {
				ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
				dataList.add(data);
				this.data = dataList;
			}
		}

		public void addInfo(JSONObject info) {
			if (info == null)
				return;
			for (String key : info.keySet()) {
				this.info.put(key, info.get(key));
			}
		}

		public void response(final boolean success, final JSONObject answer) {
			if (this.delegate == null)
				return;
			if (this.callDelegateInGameLoop) {
				if (!plugin.isEnabled())
					return;
				new BukkitRunnable() {
					@Override
					public void run() {
						delegate.response(success, answer);
					}
				}.runTask(plugin);
			} else {
				delegate.response(success, answer);
			}
		}
	}

	// thread to send web requests
	private class Postman extends Thread {
		public long sendLogsAt = 0;
		public long sendHeartBeatAt = 0;
		public long requestTokenAt = 0;

		public void run() {
			while (!this.isInterrupted()) {
				// System.out.println("postman");
				long time = System.currentTimeMillis();
				if (serverToken == null) {
					if (requestTokenAt <= time) {
						requestTokenAt = time + plugin.heartbeatSendPeriod;
						sendTokenRequest();
					}
				}
				if (sendLogsAt <= time) {
					sendLogsAt = time + plugin.logSendPeriod;
					dispatchLogQueue(true);
				}
				if (sendHeartBeatAt <= time) {
					sendHeartBeatAt = time + plugin.heartbeatSendPeriod;
					sendHeartBeat();
				}
				sendRequests(null);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	private void sendRequest(Request request) {

		/*for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (plugin.notifications && p.hasPermission("epilog.notifications")) {
				p.sendMessage("[epilog] TODO: make post request");
				if (request.data != null) {
					for (JSONObject json : request.data) {
						p.sendMessage(json.toString());
					}
				}
			}
		}
		plugin.getLogger().info("TODO: make post request");
		if (request.data != null) {
			for (JSONObject json : request.data) {
				plugin.getLogger().info(json.toString());
			}
		}*/


		Document doc = new Document();
		doc.parse(request.data.toString());

		db.sendData(doc);
	}

	
}
