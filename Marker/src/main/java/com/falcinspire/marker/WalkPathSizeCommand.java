package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class WalkPathSizeCommand implements CommandExecutor {

    final JavaPlugin plugin;

    public WalkPathSizeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sender must be a player");
            return false;
        }
        Player player = (Player) sender;

        try {
            int size = new PathStorage(plugin).getHintPaths().size();
            player.sendMessage("There are " + size + " paths recorded.");
        } catch (IOException e) {
            player.sendMessage(e.getMessage());
            e.printStackTrace();
        }

        return true;
	}
}