package io.github.hyscript7.projectfusion.keycards2.data.interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/*
 * This interface describes methods which let us work with keycards stored in Plugin Data.
 * We can fetch keycards based on various identifiers, such as an ID, an itemstack, access level, etc...
 */
public interface KeyCardAggregator {
    void create(KeyCard keyCard);
    void delete(KeyCard keyCard);
    ItemStack getItem(KeyCard keyCard);
    @Nullable KeyCard fromItem(ItemStack itemStack);
}
