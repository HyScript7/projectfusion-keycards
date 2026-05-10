package io.github.hyscript7.projectfusion.keycards.readers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@SerializableAs("Reader")
public class Reader implements ConfigurationSerializable {

    /** Where the reader block is located. */
    private final Location location;

    /** The minimum keycard level required to trigger this reader. */
    @Setter private int minimalLevel;

    /** Whether the keycard level must match exactly (not just be ≥). */
    @Setter private boolean requireExactLevelMatch;

    /** How many ticks the reader keeps its appliances active after a swipe. */
    @Setter private int pulseDurationInTicks;

    public Reader(Location location, int minimalLevel, boolean requireExactLevelMatch, int pulseDurationInTicks) {
        this.location = location;
        this.minimalLevel = minimalLevel;
        this.requireExactLevelMatch = requireExactLevelMatch;
        this.pulseDurationInTicks = pulseDurationInTicks;
    }

    // -------------------------------------------------------------------------
    // Identity — two Readers at the same block position are the same Reader
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reader other)) return false;
        return location.getBlockX() == other.location.getBlockX()
                && location.getBlockY() == other.location.getBlockY()
                && location.getBlockZ() == other.location.getBlockZ()
                && Objects.equals(location.getWorld(), other.location.getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                location.getWorld() != null ? location.getWorld().getUID() : null,
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    // -------------------------------------------------------------------------
    // YAML serialisation (kept for potential migration / YAML backend use)
    // -------------------------------------------------------------------------

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
