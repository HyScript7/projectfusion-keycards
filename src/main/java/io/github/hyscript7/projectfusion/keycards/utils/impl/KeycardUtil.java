package io.github.hyscript7.projectfusion.keycards.utils.impl;

import io.github.hyscript7.projectfusion.keycards.Keycards;
import io.github.hyscript7.projectfusion.keycards.data.Config;
import io.github.hyscript7.projectfusion.keycards.data.Keycard;
import io.github.hyscript7.projectfusion.keycards.utils.interfaces.IKeycardUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class KeycardUtil implements IKeycardUtil {
    private final Config config;
    private static final NamespacedKey KEYCARD_LEVEL_KEY = new NamespacedKey(Keycards.getPlugin(Keycards.class), "keycard_level");
    private static final NamespacedKey KEYCARD_ID_KEY = new NamespacedKey(Keycards.getPlugin(Keycards.class), "keycard_id");
    private static final NamespacedKey KEYCARD_VALIDITY_KEY = new NamespacedKey(Keycards.getPlugin(Keycards.class), "keycard_validity");

    public KeycardUtil(Config config) {
        this.config = config;
    }

    @Override
    public void createKeycard(int id, int level, String name, ItemStack item, boolean requirePermission) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(KEYCARD_LEVEL_KEY, PersistentDataType.INTEGER, level);
        itemMeta.getPersistentDataContainer().set(KEYCARD_ID_KEY, PersistentDataType.INTEGER, id);
        item.setItemMeta(itemMeta);
        Keycard keycard = new Keycard(id, level, name, item, requirePermission);
        config.getKeycards().add(keycard);
    }

    @Override
    public @Nullable ItemStack getKeycardItem(int id) {
        Keycard keycard = config.getKeycards().stream().filter(k -> k.getId() == id).findFirst().orElse(null);
        if (keycard == null) {
            return null;
        }
        ItemStack item = keycard.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(KEYCARD_VALIDITY_KEY, PersistentDataType.INTEGER, config.getValidationIndex());
        itemMeta.getPersistentDataContainer().set(KEYCARD_LEVEL_KEY, PersistentDataType.INTEGER, keycard.getLevel());
        itemMeta.getPersistentDataContainer().set(KEYCARD_ID_KEY, PersistentDataType.INTEGER, keycard.getId());
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public void deleteKeycard(int id) {
        config.getKeycards().removeIf(k -> k.getId() == id);
    }

    @Override
    public boolean isKeycardItem(ItemStack item) {
        if (!item.getItemMeta().getPersistentDataContainer().has(KEYCARD_VALIDITY_KEY)) {
            return false;
        }
        if (!item.getItemMeta().getPersistentDataContainer().has(KEYCARD_LEVEL_KEY)) {
            return false;
        }
        if (!item.getItemMeta().getPersistentDataContainer().has(KEYCARD_ID_KEY)) {
            return false;
        }
        // Check whether a keycard with that ID exists
        int id = item.getItemMeta().getPersistentDataContainer().getOrDefault(KEYCARD_ID_KEY, PersistentDataType.INTEGER, -1);
        return config.getKeycards().stream().anyMatch(k -> k.getId() == id);
    }

    @Override
    public boolean isKeycardItemValid(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(KEYCARD_VALIDITY_KEY, PersistentDataType.INTEGER, -1) == config.getValidationIndex();
    }

    @Override
    public @Nullable Keycard getKeycardFromItem(ItemStack item) {
        if (item.getItemMeta() == null) {
            return null;
        }
        int id = item.getItemMeta().getPersistentDataContainer().getOrDefault(KEYCARD_ID_KEY, PersistentDataType.INTEGER, -1);
        return config.getKeycards().stream().filter(k -> k.getId() == id).findFirst().orElse(null);
    }

    @Override
    public boolean canPlayerUseKeycard(Player player, Keycard keycard) {
        if (keycard.getPermission()) {
            return player.hasPermission("projectfusion.keycards.use." + keycard.getId());
        }
        return true;
    }
}
