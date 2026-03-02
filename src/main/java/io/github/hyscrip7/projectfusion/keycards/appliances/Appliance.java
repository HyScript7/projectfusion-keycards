package io.github.hyscrip7.projectfusion.keycards.appliances;

import io.github.hyscrip7.projectfusion.keycards.readers.Reader;
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

    public synchronized void incrementActiveReaders(Reader reader) {
        activeReaders.add(reader);
    }

    public synchronized void decrementActiveReaders(Reader reader) {
        activeReaders.remove(reader);
    }

    public synchronized boolean shouldBeInactive() {
        return activeReaders.isEmpty();
    }

    public void linkReader(Reader reader) {
        this.linkedReaders.add(reader.getLocation());
    }

    public void unlinkReader(Reader reader) {
        this.linkedReaders.remove(reader.getLocation());
    }

    public Block getBlock() {
        return location.getWorld().getBlockAt(location);
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        // We don't strictly need to save 'location' here if it's the key,
        // but keeping it makes the object self-contained.
        map.put("location", location);
        map.put("linkedReaders", new ArrayList<>(linkedReaders));
        return map;
    }

    public static Appliance deserialize(Map<String, Object> map) {
        Location loc = (Location) map.get("location");
        List<Location> readers = (List<Location>) map.get("linkedReaders");
        return new Appliance(loc, new HashSet<>(readers));
    }
}
