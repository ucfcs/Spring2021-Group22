package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public class MarkRemoveCommand implements CommandExecutor {

    final JavaPlugin plugin;

    public MarkRemoveCommand(JavaPlugin plugin) {
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
            Storage storage = new Storage(plugin);
            List<Storage.Vector> locations = storage.getLocations();
            Storage.Vector target = Tracer.trace(player.getEyeLocation(), locations);
            if (target != null) {
                locations.remove(target);
                storage.saveLocations(locations);
                sender.sendMessage("Removed target at " + target.x + " " + target.y + " " + target.z);
            } else {
                sender.sendMessage("Could not locate marker in current line of sight.");
            }
        } catch (IOException e) {
            sender.sendMessage(e.getLocalizedMessage());
            sender.sendMessage("There was an io problem when running this command. Check the server logs for details");
            e.printStackTrace();
        }
        return true;
	}
}