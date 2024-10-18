package io.github.hyscript7.projectfusion.keycards2.data.interfaces;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * This interface describes methods which let us work with keycards stored in Plugin Data.
 * We can fetch keycards based on various identifiers, such as an ID, an itemstack, access level, etc...
 */
public interface KeyCardAggregator {
    void create(int accessLevel, Component name, List<Component> lore, int customModelData);
    void delete(KeyCard keyCard);
    ItemStack getItem(KeyCard keyCard);
    @Nullable KeyCard fromItem(ItemStack itemStack);
    @Nullable KeyCard fromId(int id);
}
