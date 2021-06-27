package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MarkEditCommand implements CommandExecutor {

    final JavaPlugin plugin;

    public MarkEditCommand(JavaPlugin plugin) {
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
                Visualizer.showMarker(new Location(player.getWorld(), target.x + 0.5, target.y + 0.5, target.z + 0.5), Material.GLASS);
                Block block = player.getWorld().getBlockAt(target.x, target.y, target.z);
                if (block.getState() instanceof InventoryHolder) {
                    player.openInventory(((InventoryHolder)block.getState()).getInventory());
                } else {
                    sender.sendMessage("Block at marker (" + block.getType() + ") does not contain an inventory.");
                }
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