package com.falcinspire.wc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldController extends JavaPlugin {
	@Override
	public void onEnable() {
		getCommand("maze").setExecutor(new MazeCommand(this));
		getServer().getPluginManager().registerEvents(new SwearListener(), this);
	}
}
