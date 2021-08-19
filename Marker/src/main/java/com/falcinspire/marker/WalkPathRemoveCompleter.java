package com.falcinspire.marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class WalkPathRemoveCompleter implements TabCompleter {

    private final JavaPlugin plugin;

    public WalkPathRemoveCompleter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            try {
                int size = new PathStorage(plugin).getHintPaths().size();
                List<String> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(i + "");
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
}
