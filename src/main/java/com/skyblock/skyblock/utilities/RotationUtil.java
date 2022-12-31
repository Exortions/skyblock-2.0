package com.skyblock.skyblock.utilities;

import lombok.Data;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
// https://www.spigotmc.org/threads/rotating-particle-effects.166854/
public class RotationUtil {

    public Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y= v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    @Data
    public static class Point {

        private final double x;
        private final double y;
        private final double z;

    }

    // https://www.spigotmc.org/members/567legodude.10290/
    public List<Location> transformPoints(Location center, List<Point> points, double yaw, double pitch, double roll, double scale) {
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);

        List<Location> locations = new ArrayList<>();

        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);

        double x;
        double bx;

        double y;
        double by;

        double z;
        double bz;

        for (Point point : points) {
            x = point.getX();
            bx = x;

            y = point.getY();
            by = y;

            z = point.getZ();
            bz = z;

            x = ((x*cy-bz*sy)*cr+by*sr)*scale;
            y = ((y*cp+bz*sp)*cr-bx*sr)*scale;
            z = ((z*cp-by*sp)*cy+bx*sy)*scale;

            locations.add(new Location(center.getWorld(), center.getX() + x, center.getY() + y, center.getZ() + z));
        }

        return locations;
    }

}
