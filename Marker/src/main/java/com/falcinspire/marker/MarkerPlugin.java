package com.falcinspire.marker;

import org.bukkit.plugin.java.JavaPlugin;

public class MarkerPlugin extends JavaPlugin {

	LocationTracker locationListener = new LocationTracker();

	@Override
	public void onEnable() {
		getCommand("mark").setExecutor(new MarkCommand(this));
		getCommand("mark-at").setExecutor(new MarkAtCommand(this));
		getCommand("mark-reveal").setExecutor(new MarkRevealCommand(this));
		getCommand("mark-edit").setExecutor(new MarkEditCommand(this));
		getCommand("mark-trace").setExecutor(new MarkTraceCommand(this));
		getCommand("mark-remove").setExecutor(new MarkRemoveCommand(this));
		getCommand("walkpath-gen").setExecutor(new WalkPathGenCommand(this, locationListener));
		getCommand("walkpath-gen").setTabCompleter(new WalkPathGenCompleter(this));
		getCommand("walkpath-describe").setExecutor(new WalkPathDescribeCommand(this));
		getCommand("walkpath-describe").setTabCompleter(new WalkPathDescribeCompleter(this));
		getCommand("walkpath-size").setExecutor(new WalkPathSizeCommand(this));
		getCommand("walkpath-remove").setExecutor(new WalkPathRemoveCommand(this));
		getCommand("walkpath-remove").setTabCompleter(new WalkPathRemoveCompleter(this));
		getCommand("walkpath-install").setExecutor(new WalkPathInstallCommand(this));
		getCommand("walkpath-install").setTabCompleter(new WalkPathInstallCompleter(this));

		getServer().getPluginManager().registerEvents(locationListener, this);
	}
}
