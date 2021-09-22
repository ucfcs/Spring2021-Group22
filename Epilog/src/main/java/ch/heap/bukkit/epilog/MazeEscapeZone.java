package ch.heap.bukkit.epilog;

import org.bukkit.util.Vector;

public class MazeEscapeZone {
    String name;
    public final Vector lower;
    public final Vector higher;
    public MazeEscapeZone(String name, Vector lower, Vector higher) {
        this.name = name;
        this.lower = lower;
        this.higher = higher;
    }
}
