package ch.heap.bukkit.epilog;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

public class MazeEscapeGameData {
    private final String teamID;
    private Set<String> foundBarrels = new HashSet<>();
    private Set<String> brokenWalls = new HashSet<>();
    
    public MazeEscapeGameData(String teamID) {
        this.teamID = teamID;
    }
    
    public String getTeamID() {
        return teamID;
    }
    private String idBarrel(Location loc) {
        return loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
    }
    public boolean hasFoundBarrel(Location loc) {
        return this.foundBarrels.contains(idBarrel(loc));
    }
    public void addFoundBarrel(Location loc) {
        this.foundBarrels.add(idBarrel(loc));
    }
    public boolean hasBrokenWall(String wall) {
        return brokenWalls.contains(wall);
    }
    public void setHasBrokenWall(String wall) {
        brokenWalls.add(wall);
    }
}
