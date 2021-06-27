package com.falcinspire.marker;

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

public class Storage {

    private final JavaPlugin plugin;

    public Storage(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public List<Vector> getLocations() throws IOException {
        File file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "markers.json").toFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        List<Vector> list = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader(file));
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            int x = -1, y = -1, z = -1;
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.contentEquals("x")) {
                    x = reader.nextInt();
                } else if (name.contentEquals("y")) {
                    y = reader.nextInt();
                } else if (name.contentEquals("z")) {
                    z = reader.nextInt();
                }
            }
            list.add(new Vector(x, y, z));
            reader.endObject();
        }
        reader.endArray();
        reader.close();
        return list;
    }

    public void saveLocations(List<Vector> list) throws IOException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "markers.json").toFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        JsonWriter writer = new JsonWriter(new FileWriter(file));
        writer.beginArray();
        for (Vector vector : list) {
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
        writer.close();
    }

    public void appendLocation(Vector vector) throws IOException {
        List<Vector> current = getLocations();
        current.add(vector);
        saveLocations(current);
    }

    public void removeLocation(Vector vector) throws IOException {
        List<Vector> current = getLocations();
        current.remove(vector);
        saveLocations(current);
    }

    // public void addLocation(Vector location) {
    //     List<Vector> locations = 
    // }

    static class Vector {
        public final int x, y, z;
        public Vector(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + z;
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vector other = (Vector) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (z != other.z)
                return false;
            return true;
        }
    }
    
}
