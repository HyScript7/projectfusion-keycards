package io.github.hyscrip7.projectfusion.keycards.util;

import org.bukkit.Location;

public class LocationUtil {
    public static String toKey(Location loc) {
        return loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }
}
