package io.github.hyscript7.projectfusion.keycards2.listeners;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlock;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlockFactory;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReaderAggregator;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCardAggregator;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItem;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class CardReaderPlaceListener implements Listener {
    private final Logger logger;
    private final CardReaderAggregator cardReaderAggregator;
    private final CardReaderItemFactory cardReaderItemFactory;
    private final CardReaderBlockFactory cardReaderBlockFactory;

    public CardReaderPlaceListener(CardReaderAggregator cardReaderAggregator, CardReaderItemFactory cardReaderItemFactory, CardReaderBlockFactory cardReaderBlockFactory) {
        this.logger = KeyCardsPlugin.getPlugin(KeyCardsPlugin.class).getLogger();
        this.cardReaderAggregator = cardReaderAggregator;
        this.cardReaderItemFactory = cardReaderItemFactory;
        this.cardReaderBlockFactory = cardReaderBlockFactory;
    }

    /**
     * When we are creating a card reader, we do not check the placed block, as that could be anything that can be
     * powered by redstone or an interaction. Instead, we create a reader by right-clicking such a block with a
     * card reader tool. This event checks for such interactions.
     */
    @EventHandler
    public void blockRightClickedListener(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        ItemStack usedTool = event.getItem();
        if (clickedBlock == null) {
            // This event handles the creation of card readers, which can only happen on blocks that exist.
            return;
        }
        // TODO: Check whether the clicked block can be made into a card reader. For now we only handle iron doors.
        if (!clickedBlock.getBlockData().getMaterial().equals(Material.IRON_DOOR)) {
            return;
        }
        if (cardReaderBlockFactory.fromBlock(clickedBlock) != null) {
            // TODO: Send error message in chat
            logger.info("When handling interact event in CardReaderPlaceListener: Clicked Block is already a CardReader");
            return;
        }
        CardReaderItem cardReaderItem = cardReaderItemFactory.fromItemStack(usedTool);
        if (cardReaderItem == null) {
            // TODO: Send error message in chat
            logger.info("When handling interact event in CardReaderPlaceListener: ItemStack is not a CardReaderItem");
            return;
        }
        // Now we make the block a card reader
        cardReaderAggregator.create(cardReaderItem, clickedBlock.getLocation());
        // TODO: Handle the scenario where if the block is an iron door, we have a half to turn into a card reader above or below as well
        // ... code for that here
        // TODO: Send success message in chat
        logger.info("When handling interact event in CardReaderPlaceListener: ItemStack is not a CardReaderItem");
    }
}