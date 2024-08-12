package io.github.hyscript7.projectfusion.keycards2.data.interfaces;

import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/*
 * This interface describes methods which let us work with readers stored in Plugin Data.
 * We can fetch doors based on various identifiers, such as an ID, a block position, access level, world, etc...
 */
public interface CardReaderAggregator {
    void create(CardReaderItem cardReaderItem, Block block);
    void create(CardReaderItem cardReaderItem, Location location);
    void delete(CardReader cardReader);
    @Nullable CardReader fromLocation(Location location);
    @Nullable CardReader fromBlock(Block block);
    /*
     * Returns all blocks that should be powered when this reader is activated.
     */
    Block[] getBlocks(CardReader cardReader);
}
