package io.github.hyscrip7.projectfusion.keycards.readers;

import io.github.hyscrip7.projectfusion.keycards.ProjectFusionKeycards;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@SerializableAs("Reader")
public class Reader implements ConfigurationSerializable {
    /**
     * Where the reader is positioned.
     */
    private final Location location;

    /**
     * The minimal level
     */
    @Setter
    private int minimalLevel;
    /**
     * Whether the level needs to match exactly
     */
    @Setter
    private boolean requireExactLevelMatch;
    /**
     * For how long this reader triggers it's appliances
     */
    @Setter
    private int pulseDurationInTicks;

    public Reader(Location location, int minimalLevel, boolean requireExactLevelMatch, int pulseDurationInTicks) {
        this.location = location;
        this.minimalLevel = minimalLevel;
        this.requireExactLevelMatch = requireExactLevelMatch;
        this.pulseDurationInTicks = pulseDurationInTicks;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("minimalLevel", minimalLevel);
        map.put("requireExactLevelMatch", requireExactLevelMatch);
        map.put("pulseDurationInTicks", pulseDurationInTicks);
        return map;
    }

    public static Reader deserialize(Map<String, Object> map) {
        return new Reader(
                (Location) map.get("location"),
                (int) map.get("minimalLevel"),
                (boolean) map.get("requireExactLevelMatch"),
                (int) map.get("pulseDurationInTicks")
        );
    }
}
