package com.falcinspire.marker;

import org.bukkit.plugin.java.JavaPlugin;

public class Marker extends JavaPlugin {

	LocationTracker locationListener = new LocationTracker();

	@Override
	public void onEnable() {
		getCommand("mark").setExecutor(new MarkCommand(this));
		getCommand("markat").setExecutor(new MarkAtCommand(this));
		getCommand("markreveal").setExecutor(new MarkRevealCommand(this));
		getCommand("markedit").setExecutor(new MarkEditCommand(this));
		getCommand("marktrace").setExecutor(new MarkTraceCommand(this));
		getCommand("walkpath").setExecutor(new WalkPathCommand(this, locationListener));


		getServer().getPluginManager().registerEvents(locationListener, this);
	}
}
