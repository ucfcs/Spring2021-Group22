package com.falcinspire.marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class WalkPathGenCompleter implements TabCompleter {

    private final JavaPlugin plugin;

    public WalkPathGenCompleter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> results = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], Arrays.asList("start", "end"), results);
            return results;
        }
        return null;
    }
    
}
