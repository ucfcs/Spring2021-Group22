package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.bukkit.block.Block;

public class MarkCommand implements CommandExecutor {

    final JavaPlugin plugin;

    public MarkCommand(JavaPlugin plugin) {
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
            Block block = player.getTargetBlock(null, 50);
            if (block == null) {
                sender.sendMessage("You must be facing a block");
                return true;
            } else {
                sender.sendMessage("Marking block at " + block.getX() + " " + block.getY() + " " + block.getZ());
            }
            storage.appendLocation(new Storage.Vector(block.getX(), block.getY(), block.getZ()));
        } catch (IOException e) {
            sender.sendMessage(e.getLocalizedMessage());
            sender.sendMessage("There was an io problem when running this command. Check the server logs for details");
            e.printStackTrace();
        }
        return true;
	}
}