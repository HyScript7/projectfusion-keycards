package io.github.hyscript7.projectfusion.keycards2.items.impl;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItem;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class CardReaderItemFactoryImpl implements CardReaderItemFactory {
    private final Logger logger = KeyCardsPlugin.getPlugin(KeyCardsPlugin.class).getLogger();
    @Override
    public @Nullable CardReaderItem fromItemStack(ItemStack itemStack) {
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        if (!persistentDataContainer.has(CardReaderItemImpl.LEVEL_KEY, PersistentDataType.INTEGER))
            return null;
        if (!persistentDataContainer.has(CardReaderItemImpl.DURATION_KEY, PersistentDataType.LONG))
            return null;
        if (!persistentDataContainer.has(CardReaderItemImpl.REQUIRES_EXACT_MATCH_KEY, PersistentDataType.BOOLEAN))
            return null;
        int level;
        long duration;
        boolean requiresExactMatch;
        try {
            level = persistentDataContainer.get(CardReaderItemImpl.LEVEL_KEY, PersistentDataType.INTEGER);
            duration = persistentDataContainer.get(CardReaderItemImpl.DURATION_KEY, PersistentDataType.LONG);
            requiresExactMatch = persistentDataContainer.get(CardReaderItemImpl.REQUIRES_EXACT_MATCH_KEY, PersistentDataType.BOOLEAN);
        } catch (NullPointerException e) {
            logger.warning("Failed to get CardReaderItem from item stack. Item is probably corrupted.");
            return null;
        }
        return new CardReaderItemImpl(level, duration, requiresExactMatch);
    }

    @Override
    public @NotNull CardReaderItem fromExistingReader(CardReader cardReader) {
        int level = cardReader.getAccessLevel();
        long duration = cardReader.getDuration();
        boolean requiresExactMatch = cardReader.getRequiresExactMatch();
        return new CardReaderItemImpl(level, duration, requiresExactMatch);
    }
}
