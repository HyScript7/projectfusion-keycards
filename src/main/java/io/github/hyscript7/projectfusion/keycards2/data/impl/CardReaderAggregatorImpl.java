package io.github.hyscript7.projectfusion.keycards2.data.impl;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReaderAggregator;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.PluginData;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class CardReaderAggregatorImpl implements CardReaderAggregator {
    private final PluginData pluginData;

    public CardReaderAggregatorImpl(PluginData pluginData) {
        this.pluginData = pluginData;
    }

    @Override
    public void create(CardReaderItem cardReaderItem, Block block) {
        create(cardReaderItem, block.getLocation());
    }

    @Override
    public void create(CardReaderItem cardReaderItem, Location location) {
        int cardReaderId = 0; // TODO: Implement
        CardReader cardReader = new CardReaderImpl(
                cardReaderId,
                cardReaderItem.getLevel(),
                location,
                cardReaderItem.getDuration(),
                cardReaderItem.getRequiresExactMatch()
        );
        pluginData.getCardReaders().add(cardReader);
    }

    @Override
    public void delete(CardReader cardReader) {
        pluginData.getCardReaders().remove(cardReader);
    }

    @Override
    public @Nullable CardReader fromLocation(Location location) {
        return pluginData.getCardReaders().stream().filter(cardReader -> cardReader.getLocation().equals(location)).findFirst().orElse(null);
    }

    @Override
    public @Nullable CardReader fromBlock(Block block) {
        return fromLocation(block.getLocation());
    }

    /*
     * Wtf is this supposed to do
     */
    @Override
    public Block[] getBlocks(CardReader cardReader) {
        return new Block[0];
    }
}
