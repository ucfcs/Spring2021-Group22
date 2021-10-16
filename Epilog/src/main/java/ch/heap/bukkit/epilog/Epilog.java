package ch.heap.bukkit.epilog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

import com.mongodb.ConnectionString;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import ch.heap.bukkit.epilog.meevent.BarrelOpenedListener;
import ch.heap.bukkit.epilog.meevent.CaveListener;
import ch.heap.bukkit.epilog.meevent.CollectTrophyListener;
import ch.heap.bukkit.epilog.meevent.CrouchGreetingListener;
import ch.heap.bukkit.epilog.meevent.DunesListener;
import ch.heap.bukkit.epilog.meevent.FarmListener;
import ch.heap.bukkit.epilog.meevent.MansionListener;
import ch.heap.bukkit.epilog.meevent.SpecialItemAttackListener;
import ch.heap.bukkit.epilog.meevent.SpecialItemMarkerListener;
import ch.heap.bukkit.epilog.meevent.SpecialItemPotionListener;
import ch.heap.bukkit.epilog.meevent.SpecialItemUsageListener;
import ch.heap.bukkit.epilog.meevent.VillagerTradeListener;

public class Epilog extends JavaPlugin {
	public RemoteAPI remote;
	public PlayerNotifications informant;
	public DataCollector dataCollector;
	private EventListener listener;
	private List<Observer> observers;
	private InventoryTracker inventoryTracker;
	public ExchangeItemListener exchangeItemListener;

	private DatabaseDriver db;
	
	//TODO bring state back eventually
	// private String statePath = null;
	// private JSONObject state = null;
	public Map<String, Object> config = null;
	
	public boolean isLogging = false;
	public List<String> connectedPlugins = new ArrayList<String>();
	
	public boolean loggingEnabled = false;
	public boolean logChats = false;
	public boolean useEvaluationServer = false;
	public boolean notifications = false;
	public boolean loggingInfo = false;
	public boolean ingameCommands = false;
	public List<String> blacklist = new ArrayList<>();

	public boolean debugMode = false;
	public boolean bukkitMode = false;

	private String mongoURI = "mongodb://localhost:27017";
	
	// send log every second
	public int logSendPeriod = 1000;
	// send heartbeat every 5 min
	// TODO: REMOVE
	public int heartbeatSendPeriod = 5*60*1000;
	
	public String version = "unknown";

	public String activeExperimentLabel = null;
	
	public void versionCheck() {
		String[] v = this.getDescription().getVersion().split("-");
		this.bukkitMode = v.length>=2 && v[1].equals("bkt");
	}
	
	@Override
	public void onLoad() {
		// Setup/Load Configuration
		this.saveDefaultConfig();
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		FileConfiguration config = this.getConfig();
		this.loadConfig(config);

		// connect to DB
		db = new DatabaseDriver(new ConnectionString(mongoURI));

		this.versionCheck();
		this.version = this.getDescription().getVersion();
		// this.loadState();
		remote = new RemoteAPI(this, db);
		// remote.skippedLogs = this.state.optInt("skippedLogs", 0);
		dataCollector = new DataCollector(this);
		observers = new ArrayList<Observer>();
		// load observers (they add themselves)
		inventoryTracker = new InventoryTracker(this);
		informant = new PlayerNotifications(this);		
	}
	
	public void loadConfig(FileConfiguration config) {
		Map<String, Object> conf = this.configToMap(config);
		if (conf.equals(this.config)) return;
		boolean initial = this.config==null; 
		this.config = conf;
		LogEvent changeEvent = initial ? null : epilogStateEvent("configChange", true);
		if (changeEvent!=null && this.loggingEnabled) {
			this.postEvent(changeEvent);
			changeEvent = null;
		}
		this.loadConfig(conf);
		if (changeEvent!=null) {
			// maybe we can post now?
			this.postEvent(changeEvent);
		}
	}
	
	@Override
	public void onEnable() {
		// start communication with logging server
		remote.start();
		// register event listener
		listener = new EventListener();
		listener.epilog = this;
		getServer().getPluginManager().registerEvents(listener, this);
		getServer().getPluginManager().registerEvents(new CustomActionListener(), this);
		getServer().getPluginManager().registerEvents(new SpecialItemAttackListener(), this);
		getServer().getPluginManager().registerEvents(new SpecialItemPotionListener(this), this);
		getServer().getPluginManager().registerEvents(new SpecialItemUsageListener(this), this);
		getServer().getPluginManager().registerEvents(new SpecialItemMarkerListener(), this);
		getServer().getPluginManager().registerEvents(new FarmListener(), this);
		getServer().getPluginManager().registerEvents(new MansionListener(), this);
		getServer().getPluginManager().registerEvents(new DunesListener(), this);
		getServer().getPluginManager().registerEvents(new CaveListener(), this);
		getServer().getPluginManager().registerEvents(new CollectTrophyListener(), this);
		getServer().getPluginManager().registerEvents(new VillagerTradeListener(), this);
		getServer().getPluginManager().registerEvents(new BarrelOpenedListener(), this);
		getServer().getPluginManager().registerEvents(new CrouchGreetingListener(), this);
		exchangeItemListener = new ExchangeItemListener(this);
		getServer().getPluginManager().registerEvents(exchangeItemListener, this);
		this.getCommand("el").setExecutor(new EpilogCommandExecutor(this));
		// send onEnable to sub modules
		inventoryTracker.onEnable();

		new ArmorWatchRunnable(this).runTaskTimer(this, 0L, 20L);
		new HintWatchRunnable(this).runTaskTimer(this, 0L, 20L);
		new PlayerLocationRunnable(this).runTaskTimer(this, 0L, 20L);
	}
	
	@Override
	public void onDisable() {
		// stop listening to events
		HandlerList.unregisterAll(this);
		listener = null;
		// flush cache and stop sending data to logging server
		// might trigger new events which are ignored
		remote.stop(); 
		// this.state.put("skippedLogs", remote.skippedLogs);
		remote = null;
		observers = null;
		inventoryTracker = null;
		informant = null;
		// this.saveState();
	}
	
	public LogEvent epilogStateEvent(String trigger, boolean includeConfig) {
		LogEvent event = new LogEvent("EpilogState", System.currentTimeMillis(), activeExperimentLabel, null);
		event.data.put("trigger", trigger);
		event.data.put("onlinePlayers", this.dataCollector.getOnlinePlayers());
		if (this.config!=null) {
			boolean loggingEnabled = ((Boolean) this.config.get("loggingEnabled")).booleanValue();
			// TODO: solve more elegantly
			boolean canLog = loggingEnabled;
			if (this.isLogging!=canLog) {
				this.isLogging = canLog;
				isLoggingDidChange();
			}
			event.data.put("canLog", canLog);
			if (includeConfig) {
				event.data.put("config", this.config);
			}
		}
		return event;
	}
	
	private void isLoggingDidChange () {
		String methodName = this.isLogging ? "onLogStart" : "onLogStop";
		for (String pluginName : this.connectedPlugins) {
			Plugin plugin = this.getServer().getPluginManager().getPlugin(pluginName);
			if (plugin==null) continue;
			try {
				Method method = plugin.getClass().getMethod(methodName, (Class<?>[]) null);
				method.invoke(plugin);
			} catch (Exception e) {}
		}
	}
	
	// functions for external plugins
	
	public Class<LogEvent> connect(final Plugin plugin) {
		this.connectedPlugins.add(plugin.getName());
		if (this.isLogging) {
			new BukkitRunnable () { @Override public void run() {
				try {
					Method method = plugin.getClass().getMethod("onLogStart", (Class<?>[]) null);
					method.invoke(plugin);
				} catch (Exception e) {}
			}}.runTaskLater(plugin, 3);
		}
		return LogEvent.class;
	}
	
	public void addItemTypeStringProvider(Object obj, Method method) {
		this.dataCollector.addItemTypeStringProvider(obj, method);
	}
	
	// remote api functions
	
	public void log(Map<String, Object> data) {
		remote.addLogData(data);
	}
	
	public void postEvent(String eventName, Player player, Map <String, Object> data, boolean log) {
		LogEvent event = new LogEvent();
		event.time = System.currentTimeMillis();
		event.eventName = eventName;
		event.player = player;
		event.experimentLabel = activeExperimentLabel;
		if (data!=null) for (Entry<String, Object> entry : data.entrySet()) {
			event.data.put(entry.getKey(), entry.getValue());
		}
		event.ignore = !log;
		event.needsData = false;
		postEvent(event);
	}
	
	public void postEvent(LogEvent event) {
		if (event.needsData) {
			dataCollector.addData(event);
		}

		if (event.player != null && blacklist.contains(event.player.getUniqueId().toString())) {
			return;
		}

		// let observers handle event
		Iterator<Observer> it = observers.iterator();
		while (it.hasNext()) {
			it.next().post(event);
		}
		// send event to log server
		if (!event.ignore) {
			// needs to count skipped  events
			remote.addLogEvent(event);
		}
	}
	
	public Class<LogEvent> addEventObserver(Object obj, Method method, Collection<String> eventNames) {
		Observer observer = new Observer();
		observer.obj = obj;
		observer.method = method;
		observer.eventNames = eventNames==null ? null : new HashSet<String>(eventNames);
		observers.add(observer);
		// give reference to parameter class for reflection
		return LogEvent.class;
	}
	
	public void removeEventObserver(Object obj) {
		Iterator<Observer> it = observers.iterator();
		while (it.hasNext()) {
			if (it.next().obj==obj) {
				it.remove();
		    }
		}
	}
	
	private class Observer
	{
		public Object obj;
		public Method method;
		public HashSet<String> eventNames;
		public void post(LogEvent event) {
			String eventName = event.eventName;
			if (eventNames==null || eventNames.contains(eventName)) {
				try {
					method.invoke(obj, event);
				} catch (Exception e) {
					System.out.println("failed to invoke event: " + e);
					e.printStackTrace();
				}
			}
		}
	}
	
	// configuration helpers
	
	private Map<String, Object> configToMap(FileConfiguration config) {
		Map<String, Object> conf = new HashMap<>();
		conf.put("logSendPeriod", config.getInt("log-send-period", 10*1000)); // 10 s
		conf.put("heartbeatSendPeriod", config.getInt("heartbeat-send-period", 5*60*1000)); // 5 min
		conf.put("loggingEnabled", config.getBoolean("logging-enabled", true));
		conf.put("logChats", config.getBoolean("log-chats", true));
		conf.put("useEvaluationServer", config.getBoolean("use-evaluation-server", false));
		conf.put("notifications", config.getBoolean("player-notifications", true));
		conf.put("loggingInfo", config.getBoolean("logging-info", true));
		conf.put("ingameCommands", config.getBoolean("ingame-commands", true));
		conf.put("debugMode", config.getBoolean("debug-mode", false));
		conf.put("mongoURI", config.getString("mongo-uri", "mongodb://localhost:27017"));
		conf.put("blacklist", config.getStringList("blacklist"));
		return conf;
	}
	
	private void loadConfig(Map<String, Object> conf) {
		this.logSendPeriod = ((Integer) conf.get("logSendPeriod")).intValue();
		this.heartbeatSendPeriod = ((Integer) conf.get("heartbeatSendPeriod")).intValue();
		this.loggingEnabled = ((Boolean) conf.get("loggingEnabled")).booleanValue();
		this.logChats = ((Boolean) conf.get("logChats")).booleanValue();
		this.useEvaluationServer = ((Boolean) conf.get("useEvaluationServer")).booleanValue();
		this.notifications = ((Boolean) conf.get("notifications")).booleanValue();
		this.loggingInfo = ((Boolean) conf.get("loggingInfo")).booleanValue();
		this.ingameCommands = ((Boolean) conf.get("ingameCommands")).booleanValue();
		this.debugMode = ((Boolean) conf.get("debugMode")).booleanValue();
		this.mongoURI = ((String) conf.get("mongoURI"));
		this.blacklist = (List<String>) conf.get("blacklist");
	}

	public String getCurrentVersion() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("epilog");
		if (plugin==null) return "-1";
		return plugin.getDescription().getVersion();
	}
}
