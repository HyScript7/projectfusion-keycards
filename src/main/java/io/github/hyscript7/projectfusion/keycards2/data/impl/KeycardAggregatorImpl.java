package io.github.hyscript7.projectfusion.keycards2.data.impl;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCard;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCardAggregator;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.PluginData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class KeycardAggregatorImpl implements KeyCardAggregator {
    private static final NamespacedKey KEY_CARD_ID_KEY = new NamespacedKey(KeyCardsPlugin.getPlugin(KeyCardsPlugin.class), "key_card_id");
    private final PluginData pluginData;

    public KeycardAggregatorImpl(PluginData pluginData) {
        this.pluginData = pluginData;
    }

    private int nextId() {
        return pluginData.getKeyCards().stream().map(KeyCard::getId).max(Integer::compareTo).orElse(0) + 1;
    }

    @Override
    public void create(int accessLevel, Component name, List<Component> lore, int customModelData) {
        int keyCardId = nextId();
        KeyCard keyCard = new KeyCardImpl(keyCardId, accessLevel, name, lore, customModelData);
        pluginData.getKeyCards().add(keyCard);
    }

    @Override
    public void delete(KeyCard keyCard) {
        pluginData.getKeyCards().remove(keyCard);
    }

    @Override
    public ItemStack getItem(KeyCard keyCard) {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(keyCard.getName());
        itemMeta.lore(keyCard.getLore());

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(KEY_CARD_ID_KEY, PersistentDataType.INTEGER, keyCard.getId());

        itemStack.lore(keyCard.getLore()); // Might be redundant
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public @Nullable KeyCard fromItem(ItemStack itemStack) {
        if (Objects.isNull(itemStack)) {
            return null;
        }
        if (!itemStack.getType().equals(Material.WRITTEN_BOOK)) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if (!persistentDataContainer.has(KEY_CARD_ID_KEY, PersistentDataType.INTEGER)) {
            return null;
        }
        int keyCardId = persistentDataContainer.get(KEY_CARD_ID_KEY, PersistentDataType.INTEGER);
        return pluginData.getKeyCards().stream().filter(k -> k.getId() == keyCardId).findFirst().orElse(null);
    }

    @Override
    public @Nullable KeyCard fromId(int id) {
        return pluginData.getKeyCards().stream().filter(keyCard -> keyCard.getId() == id).findFirst().orElse(null);
    }
}
