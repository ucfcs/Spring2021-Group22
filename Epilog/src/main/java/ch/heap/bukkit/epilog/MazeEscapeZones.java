package ch.heap.bukkit.epilog;

import org.bukkit.util.Vector;

public class MazeEscapeZones {
    public static final MazeEscapeZone DUNES = new MazeEscapeZone("Dunes", new Vector(22, 52, -76), new Vector(76, 255, -11));
    public static final MazeEscapeZone FARMS = new MazeEscapeZone("Farms", new Vector(-76, 52, -76), new Vector(-2, 255, -12));
    public static final MazeEscapeZone CAVE = new MazeEscapeZone("Cave", new Vector(-76, 0, 6), new Vector(-5, 51, 76));
    public static final MazeEscapeZone FOREST = new MazeEscapeZone("Forest", new Vector(-76, 52, 6), new Vector(14, 255, 76));
    public static final MazeEscapeZone MANSION = new MazeEscapeZone("Mansion", new Vector(29, 52, 20), new Vector(76, 255, 76));
    public static final MazeEscapeZone CENTER = new MazeEscapeZone("Center", new Vector(-76, 52, -76), new Vector(76, 255, 76));
    public static final MazeEscapeZone MAZE = new MazeEscapeZone("Maze", new Vector(0, 52, 0), new Vector(9999, 255, 9999));

    public static final MazeEscapeZone WALL_1 = new MazeEscapeZone("Wall1", new Vector(-3, 55, -77), new Vector(4, 61, -71));
    public static final MazeEscapeZone WALL_2 = new MazeEscapeZone("Wall2", new Vector(-4, 59, 71), new Vector(4, 71, 77));

    public static String getPrimaryZone(Vector location) {
        if (location.getY() <= CAVE.higher.getY()) return CAVE.name;
        if (location.isInAABB(DUNES.lower, DUNES.higher)) return DUNES.name;
        if (location.isInAABB(FARMS.lower, FARMS.higher)) return FARMS.name;
        if (location.isInAABB(FOREST.lower, FOREST.higher)) return FOREST.name;
        if (location.isInAABB(MANSION.lower, MANSION.higher)) return MANSION.name;
        if (location.isInAABB(CENTER.lower, CENTER.higher)) return CENTER.name;
        return MAZE.name;
    }

    public static String getWallZone(Vector location) {
        if (location.isInAABB(WALL_1.lower, WALL_1.higher)) return WALL_1.name;
        if (location.isInAABB(WALL_2.lower, WALL_2.higher)) return WALL_2.name;
        return "unknown_wall";
    }
}
