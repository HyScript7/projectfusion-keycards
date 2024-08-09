package io.github.hyscript7.projectfusion.keycards.data;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KeycardDoor implements ConfigurationSerializable {
    private final int id;
    private final int level;
    private final Block block;
    private final int openDuration; // In seconds, default is five

    public KeycardDoor(int id, int level, Block block, int openDuration) {
        this.id = id;
        this.level = level;
        this.block = block;
        this.openDuration = openDuration;
    }

    private static Block deserializeBlock(Map<String, Object> block) {
        World w = Bukkit.getWorld(block.get("world").toString());
        assert w != null;
        int x = (int) block.get("x");
        int y = (int) block.get("y");
        int z = (int) block.get("z");
        return w.getBlockAt(x, y, z);
    }

    public static KeycardDoor deserialize(Map<String, Object> map) {
        return new KeycardDoor(
                (int) map.get("id"),
                (int) map.get("level"),
                deserializeBlock((Map<String, Object>) map.get("block")),
                (int) map.getOrDefault("open-duration", 5)
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "id", id,
                "level", level,
                "block", Map.of(
                        "x", block.getX(),
                        "y", block.getY(),
                        "z", block.getZ(),
                        "world", block.getWorld().getName()
                ),
                "open-duration", openDuration
        );
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public Block getBlock() {
        return block;
    }

    public int getOpenDuration() {
        return openDuration;
    }
}
