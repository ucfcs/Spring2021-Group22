package com.falcinspire.marker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class PathStorage {

    private final JavaPlugin plugin;

    public PathStorage(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public List<List<Vector3i>> getHintPaths() throws IOException {
        File file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "paths.json").toFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        List<List<Vector3i>> list = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader(file));
        reader.beginArray();
        while (reader.hasNext()) {
            List<Vector3i> locations = new ArrayList<>();
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                int x = -1, y = -1, z = -1;
                while (reader.hasNext()) {
                    String locationField = reader.nextName();
                    if (locationField.contentEquals("x")) {
                        x = reader.nextInt();
                    } else if (locationField.contentEquals("y")) {
                        y = reader.nextInt();
                    } else if (locationField.contentEquals("z")) {
                        z = reader.nextInt();
                    }
                }
                reader.endObject();
                locations.add(new Vector3i(x, y, z));
            }
            reader.endArray();
            list.add(locations);
        }
        reader.endArray();
        reader.close();
        return list;
    }

    public void saveHintPaths(List<List<Vector3i>> list) throws IOException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "paths.json").toFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        JsonWriter writer = new JsonWriter(new FileWriter(file));
        writer.beginArray();
        for (List<Vector3i> locations : list) {
            writer.beginArray();
            for (Vector3i vector : locations) {
                writer.beginObject();
                writer.name("x");
                writer.value(vector.x);
                writer.name("y");
                writer.value(vector.y);
                writer.name("z");
                writer.value(vector.z);
                writer.endObject();
            }
            writer.endArray();
        }
        writer.endArray();
        writer.close();
    }

    public int appendHintPath(List<Vector3i> list) throws IOException {
        List<List<Vector3i>> current = getHintPaths();
        current.add(list);
        saveHintPaths(current);
        return current.size()-1;
    }

    public void removeHintPathAtIndex(int index) throws IOException {
        List<List<Vector3i>> current = getHintPaths();
        current.remove(index);
        saveHintPaths(current);
    }
}
