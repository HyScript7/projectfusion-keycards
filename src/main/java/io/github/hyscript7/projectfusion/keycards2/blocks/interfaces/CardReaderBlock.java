package io.github.hyscript7.projectfusion.keycards2.blocks.interfaces;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import org.jetbrains.annotations.Nullable;

public interface CardReaderBlock {
    void open();
    void close();

    void destroy();

    /*
     * Returns the CardReader associated with this CardReader block.
     * null if this block is not a card reader.
     */
    @Nullable CardReader getCardReader();
}
