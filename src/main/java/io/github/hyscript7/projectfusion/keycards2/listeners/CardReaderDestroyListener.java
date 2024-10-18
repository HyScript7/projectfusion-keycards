package io.github.hyscript7.projectfusion.keycards2.listeners;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlock;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlockFactory;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReaderAggregator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.logging.Logger;

public class CardReaderDestroyListener implements Listener {
    private final Logger logger;
    private final CardReaderAggregator cardReaderAggregator;
    private final CardReaderBlockFactory cardReaderBlockFactory;

    public CardReaderDestroyListener(CardReaderAggregator cardReaderAggregator, CardReaderBlockFactory cardReaderBlockFactory) {
        this.logger = KeyCardsPlugin.getPlugin(KeyCardsPlugin.class).getLogger();
        this.cardReaderAggregator = cardReaderAggregator;
        this.cardReaderBlockFactory = cardReaderBlockFactory;
    }

    @EventHandler
    public void cardReaderBlockDestroyedListener(BlockBreakEvent event) {
        CardReaderBlock cardReaderBlock = cardReaderBlockFactory.fromBlock(event.getBlock());
        if (cardReaderBlock == null) {
            logger.info("When handling interact event in CardReaderDestroyListener: Broken block is not a card reader, ignoring!");
            return;
        }
        CardReader cardReader = cardReaderBlock.getCardReader();
        if (cardReader == null) {
            logger.info("When handling interact event in CardReaderDestroyListener: What the fuck?");
            return;
        }
        cardReaderAggregator.delete(cardReader);
        // TODO: Check whether we need to delete the reader of surrounding blocks in the case the card reader was an iron door.
        logger.info("When handling interact event in CardReaderDestroyListener: Card Reader deleted as the block was broken!");
        // TODO: Send message to player
    }
}
