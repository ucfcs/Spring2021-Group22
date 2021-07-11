package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class WalkPathCommand implements CommandExecutor {

    final JavaPlugin plugin;
    final LocationTracker locationListener;
    private String pathName;

    public WalkPathCommand(JavaPlugin plugin, LocationTracker locationListener) {
        this.plugin = plugin;
        this.locationListener = locationListener;
    }

    public File writeFile(String name, List<String> lines, World world) throws IOException {
        File parent = Paths.get(world.getWorldFolder().getAbsolutePath(), "datapacks/MazeEscape/data/mazeescape/functions", "path/").toFile();
        // File parent = Paths.get(plugin.getDataFolder().getAbsolutePath(), "path").toFile();
        // if (!parent.exists()) parent.mkdirs();
        File fileImpl = Paths.get(parent.getAbsolutePath(), name + ".mcfunction").toFile();
        fileImpl.createNewFile();
        PrintWriter writerImpl = new PrintWriter(new FileOutputStream(fileImpl));
        for (String line : lines) {
            writerImpl.println(line);
        }
        writerImpl.close();
        return fileImpl;
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sender must be a player");
            return false;
        }
        Player player = (Player) sender;

        if (args[0].contentEquals("start")) {
            this.pathName = args[1];
            player.sendMessage("Starting walk \"" + this.pathName + "\"");
            locationListener.startTracking(player);
        } else if (args[0].contentEquals("end")) {
            List<LocationTracker.Position> path = locationListener.stopTracking();
            player.sendMessage("Ending \"" + this.pathName + "\" with " + path.size() + " positions recorded");
            try {
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdir();
                }
                
                List<File> files = new ArrayList<>();

                String fileImplName = "path_" + this.pathName;
                File fileImpl = writeFile(fileImplName, path.stream().map(
                    position -> "execute positioned " + position.x + " " + position.y + " " + position.z + " if entity @p[distance=..20] run particle minecraft:soul_fire_flame ~ ~ ~ 0.1 0.1 0.1 0.01 1 normal").collect(Collectors.toList()
                ), player.getWorld());
                files.add(fileImpl);

                String bookTag = "{display:{Name:'[{\"text\":\"Hint #" + this.pathName + "\",\"color\":\"light_purple\"}]'}}";
                
                String fileScheduleName = "path_" + this.pathName + "_schedule";
                File fileSchedule = writeFile(fileScheduleName,  Arrays.asList(
                    "execute if entity @a[nbt={SelectedItem:{tag:" + bookTag + "}}] run function mazeescape:path/" + fileImplName,
                    "schedule function mazeescape:path/" + fileScheduleName + " 1s replace"
                ), player.getWorld());
                files.add(fileSchedule);

                String fileGenerateName = "path_" + this.pathName + "_generate";
                File fileGenerate = writeFile(fileGenerateName,  Arrays.asList(
                    "give @s book" + bookTag
                ), player.getWorld());
                files.add(fileGenerate);

                player.sendMessage("Created " + files.size() + " files in the data directory. Please move them to a datapack and then run the _schedule function.");
                for (File file : files) {
                    player.sendMessage(file.getAbsolutePath());
                }
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