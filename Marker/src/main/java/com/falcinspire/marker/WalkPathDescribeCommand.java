package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public class WalkPathDescribeCommand implements CommandExecutor {

    final JavaPlugin plugin;

    public WalkPathDescribeCommand(JavaPlugin plugin) {
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
            int number = Integer.parseInt(args[0]);
            if (number < 0) {
                sender.sendMessage("Number cannot be negative");
                return true;
            }
            PathStorage pathStorage = new PathStorage(plugin);
            List<List<Vector3i>> paths = pathStorage.getHintPaths();
            if (number >= paths.size()) {
                sender.sendMessage("Number must be < " + paths.size());
                return true;
            }
            List<Vector3i> path = paths.get(number);
            Vector3i start = path.get(0);
            Vector3i end = path.get(paths.size()-1);
            player.sendMessage("Path starts at (" + start.x + ", " + start.y + ", " + start.z + ") and ends at (" + end.x + ", " + end.y + ", " + end.z + ") with " + path.size() + " total locations.");
        } catch (IOException e) {
            player.sendMessage(e.getMessage());
            e.printStackTrace();
        }

        return true;
	}
}