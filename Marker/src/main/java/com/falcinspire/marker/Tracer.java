package com.falcinspire.marker;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Tracer {
    public static Storage.Vector trace(Location ray, List<Storage.Vector> locations) {
        Storage.Vector closest = null;
        double closestD2 = Double.MAX_VALUE;

        Vector rayDirection = ray.getDirection();
        Vector rayOrigin = ray.toVector();
        for (Storage.Vector vector : locations) {
            Vector sphereOrigin = new Vector(vector.x + 0.5, vector.y + 0.5, vector.z + 0.5);
            double sphereRadius = 1.0;
            Vector oc = rayOrigin.clone().subtract(sphereOrigin);
            double a = rayDirection.dot(rayDirection);
            double b = 2.0 * oc.dot(rayDirection);
            double c = oc.dot(oc) - (sphereRadius * sphereRadius);
            double discriminant = b*b - 4*a*c;
            if (discriminant + 1e-8 >= 0) {
                if (rayOrigin.distanceSquared(sphereOrigin) < closestD2) {
                    closestD2 = rayOrigin.distanceSquared(sphereOrigin);
                    closest = vector;
                }
            }
        }
        
        return closest;
    }
}
