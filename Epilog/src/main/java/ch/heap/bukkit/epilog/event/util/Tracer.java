package ch.heap.bukkit.epilog.event.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Tracer {
    public static Player trace(Player player, List<Player> locations) {
        Location ray = player.getEyeLocation();
        
        Player closest = null;
        double closestD2 = Double.MAX_VALUE;

        Vector rayDirection = ray.getDirection();
        Vector rayOrigin = ray.toVector();
        for (Player otherPlayer : locations) {
            if (player == otherPlayer) continue;
            Vector vector = otherPlayer.getEyeLocation().toVector();
            Vector sphereOrigin = new Vector(vector.getX(), vector.getY()-0.6, vector.getZ());
            double sphereRadius = 1.2;
            Vector oc = rayOrigin.clone().subtract(sphereOrigin);
            double a = rayDirection.dot(rayDirection);
            double b = 2.0 * oc.dot(rayDirection);
            double c = oc.dot(oc) - (sphereRadius * sphereRadius);
            double discriminant = b*b - 4*a*c;
            if (discriminant + 1e-8 >= 0) {
                if (rayOrigin.distanceSquared(sphereOrigin) < closestD2) {
                    closestD2 = rayOrigin.distanceSquared(sphereOrigin);
                    closest = otherPlayer;
                }
            }
        }
        
        return closest;
    }
}
