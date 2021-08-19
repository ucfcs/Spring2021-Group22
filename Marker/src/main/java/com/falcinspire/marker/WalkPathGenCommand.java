package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public class WalkPathGenCommand implements CommandExecutor {

    final JavaPlugin plugin;
    final LocationTracker locationListener;

    public WalkPathGenCommand(JavaPlugin plugin, LocationTracker locationListener) {
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
        } else {
            return false;
        }

        return true;
	}
}