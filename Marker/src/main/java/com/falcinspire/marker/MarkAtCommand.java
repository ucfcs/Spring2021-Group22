package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import org.bukkit.block.Block;

public class MarkAtCommand implements CommandExecutor {

    final JavaPlugin plugin;

    public MarkAtCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sender must be a player");
            return false;
        }

        if (args.length != 3) {
            return false;
        }

        try {
            Storage storage = new Storage(plugin);
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            storage.appendLocation(new Storage.Vector(x, y, z));
        } catch (IOException e) {
            sender.sendMessage(e.getLocalizedMessage());
            sender.sendMessage("There was an io problem when running this command. Check the server logs for details");
            e.printStackTrace();
        }
        return true;
	}
}