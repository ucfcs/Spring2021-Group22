package com.falcinspire.wc;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;

public class MazeCommand implements CommandExecutor {

    final JavaPlugin plugin;

    public MazeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sender must be a player");
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            return false;
        }
        
        File src = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + args[0]);
        if (!src.exists()) {
            sender.sendMessage(src.getAbsolutePath() + " does not exist");
            return true;
        }

        World world = player.getWorld();
        Location loc = player.getLocation();

        try {
            boolean[][] grid = new PngReader().generateBlockedFromFile(src);
            MazeBuilder.create(player.getWorld(), player.getLocation(), grid).runTaskTimer(plugin, 0L, 20L);
        } catch (IOException e) {
            player.sendMessage(e.getMessage());
            e.printStackTrace();
        }
        return true;
	}
}