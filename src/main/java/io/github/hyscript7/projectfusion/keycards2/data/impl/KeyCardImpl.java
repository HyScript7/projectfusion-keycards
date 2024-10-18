package io.github.hyscript7.projectfusion.keycards2.data.impl;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCard;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class KeyCardImpl implements KeyCard, ConfigurationSerializable {
    private int id;
    private int accessLevel;
    private Component name;
    private List<Component> lore;
    private int customModelData;

    public KeyCardImpl(int id, int accessLevel, Component name, List<Component> lore, int customModelData) {
        this.id = id;
        this.accessLevel = accessLevel;
        this.name = name;
        this.lore = lore;
        this.customModelData = customModelData;
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
    public Component getName() {
        return name;
    }

    @Override
    public List<Component> getLore() {
        return lore;
    }

    @Override
    public int getCustomModelData() {
        return customModelData;
    }

    @Override
    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public void setName(Component name) {
        this.name = name;
    }

    @Override
    public void setLore(List<Component> lore) {
        this.lore = lore;
    }

    @Override
    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        ItemStack itemStack = KeyCardsPlugin.getPlugin(KeyCardsPlugin.class).getKeyCardsConfig().getPluginData().getKeyCardAggregator().getItem(this);
        return Map.of(
                "id", id,
                "accessLevel", accessLevel,
                "itemstack", itemStack
                /*
                "name", name.toString(),
                "lore", lore.stream().map(Component::toString).toList(),
                "customModelData", customModelData
                 */
        );
    }

    public static KeyCard deserialize(Map<String, Object> map) {
        ItemStack itemStack = (ItemStack) map.get("itemstack");
        return new KeyCardImpl(
                (int) map.get("id"),
                (int) map.get("accessLevel"),
                itemStack.getItemMeta().displayName(),
                itemStack.getItemMeta().lore(),
                itemStack.getItemMeta().hasCustomModelData() ? itemStack.getItemMeta().getCustomModelData() : 0
                /*
                (Component) map.get("name"),
                (List<Component>) map.get("lore"),
                (int) map.get("customModelData")
                 */
        );
    }
}
