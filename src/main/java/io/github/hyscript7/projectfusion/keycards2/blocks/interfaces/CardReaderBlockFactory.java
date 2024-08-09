package io.github.hyscript7.projectfusion.keycards2.blocks.interfaces;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public interface CardReaderBlockFactory {
    @Nullable CardReaderBlock fromLocation(Location location);
    @Nullable CardReaderBlock fromBlock(Block block);
    CardReaderBlock fromCardReader(CardReader cardReader);
}
