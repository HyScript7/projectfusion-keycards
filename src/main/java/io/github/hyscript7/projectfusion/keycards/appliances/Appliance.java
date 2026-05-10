package io.github.hyscript7.projectfusion.keycards.appliances;

import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jspecify.annotations.NonNull;

import java.util.*;

@Getter
@SerializableAs("Appliance")
public class Appliance implements ConfigurationSerializable {

    private final Location location;
    private final Set<Location> linkedReaders;
    private volatile Set<Reader> activeReaders;

    public Appliance(Location location) {
        this.location = location;
        this.linkedReaders = new HashSet<>();
        this.activeReaders = new HashSet<>();
    }

    public Appliance(Location location, Set<Location> linkedReaders) {
        this.location = location;
        this.linkedReaders = linkedReaders;
        this.activeReaders = new HashSet<>();
    }

    // -------------------------------------------------------------------------
    // Active-reader tracking (used by ReaderManager for multi-reader appliances)
    // -------------------------------------------------------------------------

    public synchronized void incrementActiveReaders(Reader reader) {
        activeReaders.add(reader);
    }

    public synchronized void decrementActiveReaders(Reader reader) {
        activeReaders.remove(reader);
    }

    public synchronized boolean shouldBeInactive() {
        return activeReaders.isEmpty();
    }

    // -------------------------------------------------------------------------
    // Link management
    // -------------------------------------------------------------------------

    public void linkReader(Reader reader) {
        linkedReaders.add(reader.getLocation());
    }

    public void unlinkReader(Reader reader) {
        linkedReaders.remove(reader.getLocation());
    }

    public Block getBlock() {
        return location.getWorld().getBlockAt(location);
    }

    // -------------------------------------------------------------------------
    // Identity — two Appliances at the same block position are the same
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appliance other)) return false;
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
    public @NonNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("linkedReaders", new ArrayList<>(linkedReaders));
        return map;
    }

    public static Appliance deserialize(Map<String, Object> map) {
        Location loc = (Location) map.get("location");
        @SuppressWarnings("unchecked")
        List<Location> readers = (List<Location>) map.get("linkedReaders");
        return new Appliance(loc, new HashSet<>(readers));
    }
}
