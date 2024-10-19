package io.github.hyscript7.projectfusion.keycards2.listeners;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlock;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlockFactory;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReaderAggregator;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCard;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCardAggregator;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItemFactory;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * This listener handles the scenario where a card reader is clicked.
 * In essence, it checks whether the player has a KeyCard and opens the card reader if they have access.
 */
public class CardReaderInteractListener implements Listener {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CardReaderInteractListener.class);
    private final Logger logger;
    private final CardReaderAggregator cardReaderAggregator;
    private final CardReaderItemFactory cardReaderItemFactory;
    private final CardReaderBlockFactory cardReaderBlockFactory;
    private final KeyCardAggregator keyCardAggregator;

    public CardReaderInteractListener(CardReaderAggregator cardReaderAggregator, CardReaderItemFactory cardReaderItemFactory, CardReaderBlockFactory cardReaderBlockFactory, KeyCardAggregator keyCardAggregator) {
        this.logger = KeyCardsPlugin.getPlugin(KeyCardsPlugin.class).getLogger();
        this.cardReaderAggregator = cardReaderAggregator;
        this.cardReaderItemFactory = cardReaderItemFactory;
        this.cardReaderBlockFactory = cardReaderBlockFactory;
        this.keyCardAggregator = keyCardAggregator;
    }

    @EventHandler
    public void cardReaderClicked(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick()) {
            // wtf? lala
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            // This event handles interactions with card readers, which can only happen on a block that exists.
            return;
        }
        CardReaderBlock cardReaderBlock = cardReaderBlockFactory.fromBlock(clickedBlock);
        if (cardReaderBlock == null) {
            logger.info("When handling interact event in CardReaderInteractListener: Clicked Block is not a CardReader");
            return;
        }
        CardReader cardReader = cardReaderBlock.getCardReader();
        if (cardReader == null ){
            logger.info("When handling interact event in CardReaderInteractListener: The cardReader is null, which I do not know why is even possible, since I didn't document the aggregator nor my reasoning for why the return type can be null.");
            return;
        }
        @Nullable KeyCard keyCard = Arrays.stream(event.getPlayer().getInventory().getStorageContents()).map(keyCardAggregator::fromItem).filter(Objects::nonNull).max(Comparator.comparingInt(KeyCard::getAccessLevel)).orElse(null);
        // ? We may have an error in our stream logic, which as a result might return null when there is infact a keycard in the inventory.
        if (Objects.isNull(keyCard)) {
            // TODO: Send an error message to the player
            logger.info("When handling interact event in CardReaderInteractListener: Player has no KeyCard in their inventory.");
            return;
        }
        // This will not open the door if the player is sneaking and not holding a KeyCard, additionally, if we are in creative mode, we get information about the door.
        if (event.getPlayer().isSneaking()) {
            if (! (Objects.nonNull(keyCardAggregator.fromItem(event.getPlayer().getInventory().getItemInMainHand()))
                            || Objects.nonNull(keyCardAggregator.fromItem(event.getPlayer().getInventory().getItemInOffHand())))) {
                if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                    // TODO: Send the door level and other information to the player
                }
                return;
            }
        }
        // Check level and open door if levels match or is high enough
        if (cardReader.getRequiresExactMatch()) {
            if (cardReader.getAccessLevel() == keyCard.getAccessLevel()) {
                cardReaderBlock.open();
                event.setCancelled(true);
            } else {
                // TODO: Send an error message to the player
                logger.info("When handling interact event in CardReaderInteractListener: CardReader requires exact level match, but player keycard has a different level!");
            }
        } else {
            if (cardReader.getAccessLevel() <= keyCard.getAccessLevel()) {
                cardReaderBlock.open();
                event.setCancelled(true);
            } else {
                // TODO: Send an error message to the player
                logger.info("When handling interact event in CardReaderInteractListener: CardReader requires level " + cardReader.getAccessLevel() + " but player keycard only has level " + keyCard.getAccessLevel() + "! (Expecting player level to be lower than card reader level)");
            }
        }
    }
}
