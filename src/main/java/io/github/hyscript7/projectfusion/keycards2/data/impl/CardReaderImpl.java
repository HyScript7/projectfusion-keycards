package io.github.hyscript7.projectfusion.keycards2.data.impl;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CardReaderImpl implements CardReader, ConfigurationSerializable {
    private int id;
    private int accessLevel;
    private Location location;
    private Block block;
    private long duration;
    private boolean requiresExactMatch;

    public CardReaderImpl(int id, int accessLevel, Location location, long duration, boolean requiresExactMatch) {
        this.id = id;
        this.accessLevel = accessLevel;
        this.location = location;
        this.block = location.getBlock();
        this.duration = duration;
        this.requiresExactMatch = requiresExactMatch;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getAccessLevel() {
        return accessLevel;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public boolean getRequiresExactMatch() {
        return requiresExactMatch;
    }

    @Override
    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void setRequiresExactMatch(boolean requiresExactMatch) {
        this.requiresExactMatch = requiresExactMatch;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "id", id,
                "accessLevel", accessLevel,
                "location", location,
                "duration", duration,
                "requiresExactMatch", requiresExactMatch
        );
    }

    public static CardReader deserialize(Map<String, Object> map) {
        return new CardReaderImpl(
                (int) map.get("id"),
                (int) map.get("accessLevel"),
                (Location) map.get("location"),
                Long.parseLong(String.valueOf(map.get("duration"))),
                (boolean) map.get("requiresExactMatch")
        );
    }
}
