package ch.heap.bukkit.epilog.special_items;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.heap.bukkit.epilog.Epilog;

public class EpilogItemCallbackCommandExecutor implements CommandExecutor {
	public Epilog plugin;

	public EpilogItemCallbackCommandExecutor(Epilog plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 1) {
			return false;
		}
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		String itemID = args[0];
		Bukkit.getPluginManager().callEvent(new EpilogUseSpecialItemEvent(itemID, player, player.getLocation()));

		return true;
	}
}
