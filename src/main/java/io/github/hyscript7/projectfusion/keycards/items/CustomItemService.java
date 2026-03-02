package io.github.hyscript7.projectfusion.keycards.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CustomItemService {
    private final ConcurrentMap<String, CustomItem> items;

    public CustomItemService() {
        this.items = new ConcurrentHashMap<>();
    }

    public void registerItem(CustomItem customItem) {
        this.items.put(customItem.getId(), customItem);
    }

    public @Nullable CustomItem getCustomItem(ItemStack itemStack) {
        if (!itemStack.getPersistentDataContainer().has(CustomItem.CUSTOM_ITEM_ID)) {
            return null;
        }
        String customItemId = itemStack.getPersistentDataContainer().get(CustomItem.CUSTOM_ITEM_ID, PersistentDataType.STRING);
        return items.get(customItemId);
    }
}
