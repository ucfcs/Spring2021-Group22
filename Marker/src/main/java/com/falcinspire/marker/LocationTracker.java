package com.falcinspire.marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class LocationTracker implements Listener {
    
    private Player trackingPlayer;
    private Set<Position> recordedPath;

    public void startTracking(Player player) {
        this.trackingPlayer = player;
        this.recordedPath = new HashSet<>();
    }

    public List<Vector3i> stopTracking() {
        this.trackingPlayer = null;
        List<Position> list = new ArrayList<>(this.recordedPath);
        Collections.sort(list);
        List<Vector3i> returnList = new ArrayList<>(list.size());
        for (Position position : list) {
            returnList.add(new Vector3i(position.x, position.y, position.z));
        }
        return returnList;
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer() == trackingPlayer) {
            Location location = event.getTo();
            recordedPath.add(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ(), recordedPath.size()));
        }
    }

    public static class Position implements Comparable<Position> {
        final int x, y, z, id;

        public Position(int x, int y, int z, int id) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.id = id;
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
            Position other = (Position) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (z != other.z)
                return false;
            return true;
        }

        @Override
        public int compareTo(Position o) {
            return Integer.compare(id, o.id);
        }
        
    }
}
