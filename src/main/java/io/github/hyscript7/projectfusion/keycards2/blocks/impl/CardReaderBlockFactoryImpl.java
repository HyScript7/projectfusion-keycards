package io.github.hyscript7.projectfusion.keycards2.blocks.impl;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlock;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlockFactory;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class CardReaderBlockFactoryImpl implements CardReaderBlockFactory {
    @Override
    public @Nullable CardReaderBlock fromLocation(Location location) {
        @Nullable CardReader cardReader = KeyCardsPlugin.getPlugin(KeyCardsPlugin.class).getKeyCardsConfig().getPluginData().getCardReaderAggregator().fromLocation(location);
        if (cardReader == null) {
            return null;
        }
        return fromCardReader(cardReader);
    }

    @Override
    public @Nullable CardReaderBlock fromBlock(Block block) {
        return fromLocation(block.getLocation());
    }

    @Override
    public CardReaderBlock fromCardReader(CardReader cardReader) {
        return new CardReaderBlockImpl(cardReader);
    }
}
