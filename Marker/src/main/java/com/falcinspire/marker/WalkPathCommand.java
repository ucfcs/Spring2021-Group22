package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public class WalkPathCommand implements CommandExecutor {

    final JavaPlugin plugin;
    final LocationTracker locationListener;

    public WalkPathCommand(JavaPlugin plugin, LocationTracker locationListener) {
        this.plugin = plugin;
        this.locationListener = locationListener;
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sender must be a player");
            return false;
        }
        Player player = (Player) sender;

        if (args[0].contentEquals("start")) {
            player.sendMessage("Start walking. When finished, run /" + label + " end.");
            locationListener.startTracking(player);
        } else if (args[0].contentEquals("end")) {
            List<Vector3i> path = locationListener.stopTracking();
            try {
                int number = new PathStorage(plugin).appendHintPath(path);
                player.sendMessage("Ending path #" + number + " with " + path.size() + " positions recorded.");
            } catch (IOException e) {
                player.sendMessage(e.getMessage());
                e.printStackTrace();
            }
        } else if (args[0].contentEquals("size")) {
            try {
                int size = new PathStorage(plugin).getHintPaths().size();
                player.sendMessage("There are " + size + " paths recorded.");
            } catch (IOException e) {
                player.sendMessage(e.getMessage());
                e.printStackTrace();
            }
        } else if (args[0].contentEquals("describe")) {
            try {
                int number = Integer.parseInt(args[1]);
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
        } else if (args[0].contentEquals("remove")) {
            try {
                int number = Integer.parseInt(args[1]);
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
                paths.remove(number);
                pathStorage.saveHintPaths(paths);
                player.sendMessage("There are now " + paths.size() + " paths recorded.");
            } catch (IOException e) {
                player.sendMessage(e.getMessage());
                e.printStackTrace();
            }
        } else {
            return false;
        }

        return true;
	}
}