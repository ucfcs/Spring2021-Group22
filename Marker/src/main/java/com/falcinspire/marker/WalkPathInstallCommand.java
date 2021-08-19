package com.falcinspire.marker;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.World;

public class WalkPathInstallCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public WalkPathInstallCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void writeFile(File file, List<String> lines) throws IOException {
        file.createNewFile();
        PrintWriter writerImpl = new PrintWriter(new FileOutputStream(file));
        for (String line : lines) {
            writerImpl.println(line);
        }
        writerImpl.close();
    }

    public String getImplName(int index) {
        return "path_" + index;
    }

    public String getScheduleName(int index) {
        return "path_" + index + "_schedule";
    }

    public String getGenerateName(int index) {
        return "path_" + index + "_generate";
    }

    public File getPathFile(String name, World world) {
        return Paths.get(
            world.getWorldFolder().getAbsolutePath(), 
            "datapacks/MazeEscape/data/mazeescape/functions", 
            "path", 
            name + ".mcfunction"
        ).toFile();
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sender must be a player");
            return false;
        }
        Player player = (Player) sender;

        if (args.length < 2) {
            return false;
        }
        
        int number = Integer.parseInt(args[1]);
        if (number < 0) {
            sender.sendMessage("Number cannot be negative");
            return true;
        }

        String type = args[0];
        if (type.contentEquals("install")) {
            String particle = args.length > 2 ? args[2] : "minecraft:soul_fire_flame";
    
            try {
                List<List<Vector3i>> paths = new PathStorage(plugin).getHintPaths();
                if (number >= paths.size()) {
                    sender.sendMessage("Number must be an index < " + paths.size());
                    return true;
                }
        
                List<Vector3i> path = paths.get(number);
        
                List<File> files = new ArrayList<>();
                
                String fileImplName = getImplName(number);
                File fileImpl = getPathFile(fileImplName, player.getWorld());
                writeFile(fileImpl, path.stream().map(
                    position -> "execute positioned " + position.x + " " + position.y + " " + position.z + " if entity @p[distance=..20] run particle " + particle + " ~ ~ ~ 0.1 0.1 0.1 0.01 1 normal").collect(Collectors.toList()
                ));
                files.add(fileImpl);
        
                String bookTag = "{display:{Name:'[{\"text\":\"Hint #" + number + "\",\"color\":\"light_purple\"}]'}}";
                
                String fileScheduleName = getScheduleName(number);
                File fileSchedule = getPathFile(fileScheduleName, player.getWorld());
                writeFile(fileSchedule,  Arrays.asList(
                    "execute if entity @a[nbt={SelectedItem:{tag:" + bookTag + "}}] run function mazeescape:path/" + fileImplName,
                    "schedule function mazeescape:path/" + fileScheduleName + " 1s replace"
                ));
                files.add(fileSchedule);
        
                String fileGenerateName = getGenerateName(number);
                File fileGenerate = getPathFile(fileGenerateName, player.getWorld());
                writeFile(fileGenerate,  Arrays.asList(
                    "give @s book" + bookTag
                ));
                files.add(fileGenerate);
        
                player.sendMessage("Created " + files.size() + " files in the mazeescape datapack. Please finish this process by running /minecraft:reload followed by the function mazeescape:path/" + fileScheduleName);
                for (File file : files) {
                    player.sendMessage(file.getAbsolutePath());
                }
            } catch (IOException e) {
                sender.sendMessage(e.getMessage());
                e.printStackTrace();
            }
            return true;
        } else if (type.contentEquals("uninstall")) {
            List<String> filePaths = new ArrayList<>();

            String fileImplName = getImplName(number);
            File fileImpl = getPathFile(fileImplName, player.getWorld());
            filePaths.add(fileImpl.getAbsolutePath());
            fileImpl.delete();

            String fileScheduleName = getScheduleName(number);
            File fileSchedule = getPathFile(fileScheduleName, player.getWorld());
            filePaths.add(fileSchedule.getAbsolutePath());
            fileSchedule.delete();
    
            String fileGenerateName = getGenerateName(number);
            File fileGenerate = getPathFile(fileGenerateName, player.getWorld());
            filePaths.add(fileGenerate.getAbsolutePath());
            fileGenerate.delete();
    
            player.sendMessage("Deleted " + filePaths.size() + " files in the mazeescape datapack.");
            for (String filePath : filePaths) {
                player.sendMessage(filePath);
            }
        } else {
            sender.sendMessage("Unknown operation " + type);
            return true;
        }
        
        return true;
    }
}
