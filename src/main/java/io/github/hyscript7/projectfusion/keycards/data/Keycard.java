package io.github.hyscript7.projectfusion.keycards.data;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Keycard implements ConfigurationSerializable {
    private final int id;
    private final int level;
    private final String name;
    private final ItemStack item;
    private final boolean permission;

    public Keycard(int id, int level, String name, ItemStack item, boolean permission) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.item = item;
        this.permission = permission;
    }

    public static Keycard deserialize(Map<String, Object> map) {
        return new Keycard(
                (int) map.get("id"),
                (int) map.get("level"),
                (String) map.get("name"),
                ItemStack.deserialize((Map<String, Object>) map.get("item")),
                (boolean) map.get("permission")
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "id", id,
                "level", level,
                "name", name,
                "item", item.serialize(),
                "permission", permission
        );
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean getPermission() {
        return permission;
    }
}
