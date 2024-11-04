package io.github.hyscript7.projectfusion.keycards2.listeners;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlock;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlockFactory;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReaderAggregator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
        deleteCardReaderIfExistsAt(event.getBlock(), 0);
    }

    private void deleteCardReaderIfExistsAt(Block block, int depth) {
        CardReaderBlock cardReaderBlock = cardReaderBlockFactory.fromBlock(block);
        if (cardReaderBlock == null) {
            logger.info("When handling interact event in CardReaderDestroyListener: Broken block is not a card reader, ignoring!");
            return;
        }
        CardReader cardReader = cardReaderBlock.getCardReader();
        if (cardReader == null) {
            logger.info("When handling interact event in CardReaderDestroyListener: What the fuck?");
            return;
        }
        // TODO: There is a destroy method in CardReaderBlock, move this logic there
        cardReaderAggregator.delete(cardReader);
        // If this is an iron door, delete the other half as well.
        if (block.getBlockData().getMaterial().equals(Material.IRON_DOOR) && depth < 2) {
            // Someone is going to find a way to crash the server with this.
            Block above = block.getRelative(BlockFace.UP);
            deleteCardReaderIfExistsAt(above, depth+1);
            Block below = block.getRelative(BlockFace.DOWN);
            deleteCardReaderIfExistsAt(below, depth+1);
        }
        logger.info("When handling interact event in CardReaderDestroyListener: Card Reader deleted as the block was broken!");
        // TODO: Send message to player
    }
}
