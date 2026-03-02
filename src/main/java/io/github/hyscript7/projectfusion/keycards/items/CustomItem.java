package io.github.hyscript7.projectfusion.keycards.items;

import io.github.hyscript7.projectfusion.keycards.ProjectFusionKeycards;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CustomItem {
    public static final NamespacedKey CUSTOM_ITEM_ID = new NamespacedKey(ProjectFusionKeycards.namespace, "item_id");
    private final String id;
    private final NamespacedKey model;
    private final Component name;
    private final List<Component> lore;

    public CustomItem(String id, NamespacedKey model, Component name, List<Component> lore) {
        this.id = id;
        this.model = model;
        this.name = name;
        this.lore = lore;
    }

    public String getId() {
        return id;
    }

    public ItemStack createItemStack() {
        ItemStack stack = new ItemStack(Material.JIGSAW, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.itemName(name);
        meta.lore(lore);
        meta.getPersistentDataContainer().set(CUSTOM_ITEM_ID, PersistentDataType.STRING, id);
        meta.setItemModel(model);
        customizeMeta(meta);
        stack.setItemMeta(meta);
        customizeItem(stack);
        return stack;
    }

    /**
     * Override this method in custom items if you need to further modify the item meta of the underlying item
     * @param itemMeta The item meta before it is applied
     */
    protected void customizeMeta(ItemMeta itemMeta) {}

    /**
     * Override this method in custom items if you need to further modify the created item after the meta has been attached
     * @param itemStack The item stack before it is returned
     */
    protected void customizeItem(ItemStack itemStack) {}

    public abstract void onLeftClick(@NotNull ItemStack itemStack, @NotNull Player player, @Nullable Block clickedBlock, @Nullable Location clickedLocation, boolean isOffHanded);

    public abstract void onRightClick(@NotNull ItemStack itemStack, @NotNull Player player, @Nullable Block clickedBlock, @Nullable Location clickedLocation, boolean isOffHanded);
}
