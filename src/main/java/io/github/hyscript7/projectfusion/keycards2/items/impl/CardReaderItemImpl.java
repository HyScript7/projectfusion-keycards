package io.github.hyscript7.projectfusion.keycards2.items.impl;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CardReaderItemImpl implements CardReaderItem {
    public static final NamespacedKey LEVEL_KEY = new NamespacedKey(KeyCardsPlugin.getPlugin(KeyCardsPlugin.class), "level");
    public static final NamespacedKey DURATION_KEY = new NamespacedKey(KeyCardsPlugin.getPlugin(KeyCardsPlugin.class), "duration");
    public static final NamespacedKey REQUIRES_EXACT_MATCH_KEY = new NamespacedKey(KeyCardsPlugin.getPlugin(KeyCardsPlugin.class), "requires_exact_match");

    private final int level;
    private final long duration;
    private final boolean requiresExactMatch;

    public CardReaderItemImpl(int level, long duration, boolean requiresExactMatch) {
        this.level = level;
        this.duration = duration;
        this.requiresExactMatch = requiresExactMatch;
    }

    @Override
    public int getLevel() {
        return level;
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
    public ItemStack asItemStack() {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        updateDisplayData(itemMeta);
        updatePersistentData(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void updateDisplayData(ItemMeta itemMeta) {
        List<Component> lore = List.of(
                Component.text("Level: " + level),
                Component.text("Open Duration: " + duration + "s"),
                Component.text("Requires exact match: ").append(requiresExactMatch ? Component.text("Yes").color(TextColor.color(0x00FF00)) : Component.text("No").color(TextColor.color(0xFF0000)))
        );
        itemMeta.lore(lore);
        Component displayName = Component.text("Card Reader (Lv. " + level + ")");
        itemMeta.displayName(displayName);
    }

    private void updatePersistentData(ItemMeta itemMeta) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(LEVEL_KEY, PersistentDataType.INTEGER, level);
        persistentDataContainer.set(DURATION_KEY, PersistentDataType.LONG, duration);
        persistentDataContainer.set(REQUIRES_EXACT_MATCH_KEY, PersistentDataType.BOOLEAN, requiresExactMatch);
    }
}
